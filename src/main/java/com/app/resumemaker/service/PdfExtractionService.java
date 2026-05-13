package com.app.resumemaker.service;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;

@Service
public class PdfExtractionService {

    public Map<String, Object> extract(InputStream pdfStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            List<Map<String, Object>> pagesData = new ArrayList<>();
            Map<String, String> fontMap = new HashMap<>(); // unique font ID -> font metadata

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage page = document.getPage(i);
                pagesData.add(extractPage(page, i, document, fontMap));

                // Extract Fonts for this page
                extractFonts(page, fontMap);
            }

            // Convert fontMap to required list format
            List<Map<String, String>> fontsList = new ArrayList<>();
            for (Map.Entry<String, String> entry : fontMap.entrySet()) {
                Map<String, String> f = new HashMap<>();
                f.put("name", entry.getKey());
                f.put("data", entry.getValue());
                fontsList.add(f);
            }

            return Map.of(
                    "pages", pagesData,
                    "fonts", fontsList);
        }
    }

    private void extractFonts(PDPage page, Map<String, String> fontMap) throws IOException {
        for (COSName fontName : page.getResources().getFontNames()) {
            try {
                PDFont font = page.getResources().getFont(fontName);
                if (font == null)
                    continue;
                String name = font.getName();
                if (!fontMap.containsKey(name) && font.getFontDescriptor() != null) {
                    InputStream fontStream = null;
                    if (font.getFontDescriptor().getFontFile() != null) {
                        fontStream = font.getFontDescriptor().getFontFile().createInputStream();
                    } else if (font.getFontDescriptor().getFontFile2() != null) {
                        fontStream = font.getFontDescriptor().getFontFile2().createInputStream();
                    } else if (font.getFontDescriptor().getFontFile3() != null) {
                        fontStream = font.getFontDescriptor().getFontFile3().createInputStream();
                    }

                    if (fontStream != null) {
                        try (InputStream fs = fontStream) {
                            byte[] fontBytes = fs.readAllBytes();
                            String base64 = Base64.getEncoder().encodeToString(fontBytes);
                            fontMap.put(name, base64);
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore individual font extraction errors
                System.err.println("Error extracting font " + fontName + ": " + e.getMessage());
            }
        }
    }

    private Map<String, Object> extractPage(PDPage page, int pageIndex, PDDocument doc, Map<String, String> fontMap)
            throws IOException {
        // 1. Setup extractors
        PageContentCollector collector = new PageContentCollector(page);
        collector.processPage(page); // Execute extraction

        // 2. Setup Text Extractor
        CustomTextStripper textStripper = new CustomTextStripper();
        textStripper.setStartPage(pageIndex + 1);
        textStripper.setEndPage(pageIndex + 1);
        textStripper.getText(doc); // triggers writeString

        // 3. Merge Items
        List<Map<String, Object>> allItems = new ArrayList<>();
        allItems.addAll(collector.getItems());
        allItems.addAll(textStripper.getTextItems());

        // 4. Build Response
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("page_index", pageIndex);
        pageMap.put("width", page.getMediaBox().getWidth());
        pageMap.put("height", page.getMediaBox().getHeight());

        // Extract Annotations
        List<Map<String, Object>> annotations = extractAnnotations(page);
        pageMap.put("annotations", annotations);

        // Logo Detection Heuristic
        detectLogos(allItems, annotations);

        pageMap.put("items", allItems);

        return pageMap;
    }

    private List<Map<String, Object>> extractAnnotations(PDPage page) throws IOException {
        List<Map<String, Object>> annotations = new ArrayList<>();
        if (page.getAnnotations() == null)
            return annotations;

        for (PDAnnotation annotation : page.getAnnotations()) {
            Map<String, Object> ann = new HashMap<>();
            ann.put("type", annotation.getSubtype());
            ann.put("contents", annotation.getContents());

            PDRectangle rect = annotation.getRectangle();
            if (rect != null) {
                ann.put("rect", List.of(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(),
                        rect.getUpperRightY()));
            }

            if (annotation instanceof PDAnnotationLink) {
                PDAnnotationLink link = (PDAnnotationLink) annotation;
                PDAction action = link.getAction();
                if (action instanceof PDActionURI) {
                    ann.put("uri", ((PDActionURI) action).getURI());
                }
            }
            annotations.add(ann);
        }
        return annotations;
    }

    private void detectLogos(List<Map<String, Object>> items, List<Map<String, Object>> annotations) {
        for (Map<String, Object> item : items) {
            if (!item.containsKey("bbox"))
                continue;
            @SuppressWarnings("unchecked")
            List<Float> bbox = (List<Float>) item.get("bbox");
            if (bbox == null || bbox.size() < 4)
                continue;

            float ix1 = bbox.get(0);
            float iy1 = bbox.get(1);
            float ix2 = bbox.get(2);
            float iy2 = bbox.get(3);

            for (Map<String, Object> ann : annotations) {
                if (!"Link".equals(ann.get("type")) || !ann.containsKey("uri"))
                    continue;
                if (!ann.containsKey("rect"))
                    continue;

                @SuppressWarnings("unchecked")
                List<Float> rect = (List<Float>) ann.get("rect");
                float ax1 = rect.get(0);
                float ay1 = rect.get(1);
                float ax2 = rect.get(2);
                float ay2 = rect.get(3);

                // Check intersection
                if (ix1 < ax2 && ix2 > ax1 && iy1 < ay2 && iy2 > ay1) {
                    String uri = (String) ann.get("uri");
                    if (uri == null)
                        continue;

                    item.put("link_target", uri);
                    String lowerUri = uri.toLowerCase();
                    if (lowerUri.contains("linkedin")) {
                        item.put("logo", "linkedin");
                    } else if (lowerUri.contains("github")) {
                        item.put("logo", "github");
                    } else if (lowerUri.contains("twitter") || lowerUri.contains("x.com")) {
                        item.put("logo", "twitter");
                    } else if (lowerUri.contains("facebook")) {
                        item.put("logo", "facebook");
                    } else if (lowerUri.contains("instagram")) {
                        item.put("logo", "instagram");
                    }
                }
            }
        }
    }

    // Convert PDF colors to standardized RGB array
    private static float[] toRGB(float[] components) {
        if (components == null)
            return new float[] { 0, 0, 0 };

        if (components.length == 1) {
            // Grayscale
            return new float[] { components[0], components[0], components[0] };
        } else if (components.length == 3) {
            // RGB
            return components;
        } else if (components.length == 4) {
            // CMYK -> RGB (Approximation)
            float c = components[0];
            float m = components[1];
            float y = components[2];
            float k = components[3];

            float r = (1 - c) * (1 - k);
            float g = (1 - m) * (1 - k);
            float b = (1 - y) * (1 - k);
            return new float[] { r, g, b };
        }
        return components; // Fallback
    }

    // Inner class to handle Graphics events (Paths, Images)
    private static class PageContentCollector extends PDFGraphicsStreamEngine {
        private final List<Map<String, Object>> items = new ArrayList<>();

        protected PageContentCollector(PDPage page) {
            super(page);
        }

        @Override
        public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
            Map<String, Object> rect = new HashMap<>();
            rect.put("type", "path");
            rect.put("op", "re");
            rect.put("pts", List.of(
                    Map.of("x", p0.getX(), "y", p0.getY()),
                    Map.of("x", p1.getX(), "y", p1.getY()),
                    Map.of("x", p2.getX(), "y", p2.getY()),
                    Map.of("x", p3.getX(), "y", p3.getY())));
            items.add(rect);
        }

        @Override
        public void drawImage(PDImage pdImage) throws IOException {
            Map<String, Object> img = new HashMap<>();
            img.put("type", "image");

            Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
            // In PDF, an image is drawn in a 1x1 unit square context, transformed by CTM.
            img.put("matrix", List.of(ctm.getScaleX(), ctm.getShearY(), ctm.getShearX(), ctm.getScaleY(),
                    ctm.getTranslateX(), ctm.getTranslateY()));

            float x = ctm.getTranslateX();
            float y = ctm.getTranslateY();
            float w = ctm.getScaleX();
            float h = ctm.getScaleY();

            img.put("x", x);
            img.put("y", y);
            img.put("width", w);
            img.put("height", h);
            img.put("bbox", List.of(x, y, x + w, y + h));

            // Extract Base64 Image
            try {
                BufferedImage image = pdImage.getImage();
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                    img.put("data", base64);
                } else {
                    img.put("error", "BufferedImage is null");
                }
            } catch (Exception e) {
                img.put("error", "Image extraction failed: " + e.getMessage());
            }

            items.add(img);
        }

        public void clip(int windingRule) throws IOException {
            items.add(Map.of("type", "clip", "winding", windingRule));
        }

        @Override
        public void moveTo(float x, float y) throws IOException {
            items.add(Map.of("type", "path_move", "x", x, "y", y));
        }

        @Override
        public void lineTo(float x, float y) throws IOException {
            items.add(Map.of("type", "path_line", "x", x, "y", y));
        }

        @Override
        public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
            Map<String, Object> op = new HashMap<>();
            op.put("type", "path_curve");
            op.put("pts", List.of(x1, y1, x2, y2, x3, y3));
            items.add(op);
        }

        @Override
        public Point2D getCurrentPoint() throws IOException {
            return new Point2D.Float(0, 0);
        }

        @Override
        public void closePath() throws IOException {
            items.add(Map.of("type", "path_close"));
        }

        @Override
        public void endPath() throws IOException {
        }

        @Override
        public void strokePath() throws IOException {
            PDColor color = getGraphicsState().getStrokingColor();
            float width = getGraphicsState().getLineWidth();
            items.add(Map.of("type", "stroke", "color", toRGB(color.getComponents()), "width", width));
        }

        @Override
        public void fillPath(int windingRule) throws IOException {
            PDColor color = getGraphicsState().getNonStrokingColor();
            items.add(Map.of("type", "fill", "color", toRGB(color.getComponents())));
        }

        @Override
        public void fillAndStrokePath(int windingRule) throws IOException {
            PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
            items.add(Map.of("type", "fill", "color", toRGB(nonStrokingColor.getComponents())));

            PDColor strokingColor = getGraphicsState().getStrokingColor();
            float width = getGraphicsState().getLineWidth();
            items.add(Map.of("type", "stroke", "color", toRGB(strokingColor.getComponents()), "width", width));
        }

        @Override
        public void shadingFill(org.apache.pdfbox.cos.COSName shadingName) throws IOException {
            PDShading shading = getResources().getShading(shadingName);
            Map<String, Object> shade = new HashMap<>();
            shade.put("type", "shading");
            shade.put("shadingName", shadingName.getName());
            if (shading != null) {
                shade.put("shadingType", shading.getType());
                if (shading.getBBox() != null) {
                    PDRectangle bbox = shading.getBBox();
                    shade.put("bbox", List.of(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(),
                            bbox.getUpperRightY()));
                }
                if (shading.getColorSpace() != null) {
                    shade.put("colorSpace", shading.getColorSpace().getName());
                }
            }
            items.add(shade);
        }

        public List<Map<String, Object>> getItems() {
            return items;
        }
    }

    // Custom Text Stripper for Text Extraction with coordinates
    public static class CustomTextStripper extends PDFTextStripper {
        private final List<Map<String, Object>> textItems = new ArrayList<>();
        private final Map<TextPosition, PDColor> colorMap = new IdentityHashMap<>();

        public CustomTextStripper() throws IOException {
            super();
            setSortByPosition(true); // Ensure logical reading order
        }

        @Override
        protected void processTextPosition(TextPosition text) {
            try {
                // Capture the color state for this specific text position
                PDColor color = getGraphicsState().getNonStrokingColor();
                colorMap.put(text, color);
            } catch (Exception e) {
                // ignore
            }
            super.processTextPosition(text);
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
            for (TextPosition text : textPositions) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", "text");
                item.put("content", text.getUnicode());

                // Construct BBox
                float x = text.getXDirAdj();
                float y = text.getYDirAdj();
                float w = text.getWidthDirAdj();
                float h = text.getHeightDir();

                item.put("bbox", List.of(x, y, x + w, y + h));
                item.put("origin", List.of(x, y));

                // Font handling
                PDFont font = text.getFont();
                item.put("font", font.getName());
                item.put("raw_font", font.getName());
                item.put("size", text.getFontSizeInPt());

                // Matrix
                item.put("matrix", List.of(1, 0, 0, 1, 0, 0));

                // Flags
                item.put("is_bold", font.getName().toLowerCase().contains("bold"));
                item.put("is_italic", font.getName().toLowerCase().contains("italic"));

                // Color Extraction with Normalization
                PDColor color = colorMap.get(text);
                if (color != null) {
                    item.put("color", toRGB(color.getComponents()));
                } else {
                    item.put("color", new float[] { 0, 0, 0 });
                }

                textItems.add(item);
            }
        }

        public List<Map<String, Object>> getTextItems() {
            return textItems;
        }
    }
}
