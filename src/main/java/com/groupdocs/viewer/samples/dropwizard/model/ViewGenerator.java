package com.groupdocs.viewer.samples.dropwizard.model;

import com.groupdocs.viewer.converter.options.HtmlOptions;
import com.groupdocs.viewer.converter.options.ImageOptions;
import com.groupdocs.viewer.domain.FileDescription;
import com.groupdocs.viewer.domain.Transformation;
import com.groupdocs.viewer.domain.WatermarkPosition;
import com.groupdocs.viewer.domain.containers.FileContainer;
import com.groupdocs.viewer.domain.containers.FileTreeContainer;
import com.groupdocs.viewer.domain.html.PageHtml;
import com.groupdocs.viewer.domain.image.PageImage;
import com.groupdocs.viewer.domain.options.FileTreeOptions;
import com.groupdocs.viewer.domain.options.PdfFileOptions;
import com.groupdocs.viewer.handler.ViewerHandler;
import com.groupdocs.viewer.handler.ViewerHtmlHandler;
import com.groupdocs.viewer.handler.ViewerImageHandler;
import com.groupdocs.viewer.samples.dropwizard.model.business.HtmlInfo;
import com.groupdocs.viewer.samples.dropwizard.model.business.ImageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type View generator.
 * @author Aleksey Permyakov (12.04.2016).
 */
public class ViewGenerator {

    private static com.groupdocs.viewer.config.ViewerConfig config;

    /**
     * Init generator.
     * @param viewerConfig the viewer config
     */
    public static void initGenerator(com.groupdocs.viewer.config.ViewerConfig viewerConfig) {
        ViewGenerator.config = viewerConfig;
    }

    /**
     * Render simple document in html representation
     * @param documentName     File name
     * @param DocumentPassword Optional
     * @return the list
     * @throws Exception the exception
     */
    public static List<HtmlInfo> renderDocumentAsHtml(String documentName, String DocumentPassword) throws Exception {

        // Create html handler
        ViewerHtmlHandler htmlHandler = new ViewerHtmlHandler(config);

        //Instantiate the HtmlOptions object
        HtmlOptions options = new HtmlOptions();

        //to get html representations of pages with embedded resources
        options.setResourcesEmbedded(true);

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Get document pages in html form
        List<PageHtml> pages = htmlHandler.getPages(documentName, options);

        return getHtmlInfos(pages);
        //ExEnd:RenderAsHtml
    }

    /**
     * Render document in html representation with watermark
     * @param DocumentName     file/document name
     * @param WatermarkText    watermark text
     * @param WatermarkColor   System.Drawing.Color
     * @param WatermarkWidth   width of watermark as integer. it is optional Parameter default value is 100
     * @param DocumentPassword Password Parameter is optional
     * @return the list
     * @throws Exception the exception
     */
    public static List<HtmlInfo> renderDocumentAsHtml(String DocumentName, String WatermarkText, Color WatermarkColor, int WatermarkWidth, String DocumentPassword) throws Exception {
        // Guid implies that unique document name
        // Create html handler
        ViewerHtmlHandler htmlHandler = new ViewerHtmlHandler(config);
        //Instantiate the HtmlOptions object
        HtmlOptions options = new HtmlOptions();
        options.setResourcesEmbedded(false);
        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }
        // Call AddWatermark and pass the reference of HtmlOptions object as 1st parameter
        Utilities.PageTransformations.addWatermark(options, WatermarkText, WatermarkColor, WatermarkPosition.Diagonal, WatermarkWidth);
        //Get document pages in html form
        List<PageHtml> pages = htmlHandler.getPages(DocumentName, options);

        return getHtmlInfos(pages);
        //ExEnd:RenderAsHtmlWithWaterMark
    }

    /**
     * Rotate document as html list.
     * @param DocumentName     the document name
     * @param pageNumber       the page number
     * @param RotationAngle    the rotation angle
     * @param DocumentPassword the document password
     * @return the list
     * @throws Exception the exception
     */
    public static List<HtmlInfo> rotateDocumentAsHtml(String DocumentName, int pageNumber, int RotationAngle, String DocumentPassword) throws Exception {
        // Guid implies that unique document name
        // Create image handler
        ViewerHandler handler = new ViewerHtmlHandler(config);
        //Initialize ImageOptions Object and setting Rotate Transformation
        HtmlOptions options = new HtmlOptions();
        options.setTransformations(Transformation.Rotate);

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Call RotatePages to apply rotate transformation to a page
        Utilities.PageTransformations.rotatePages(handler, DocumentName, pageNumber, RotationAngle);

        //down cast the handler(ViewerHandler) to viewerHtmlHandler
        ViewerHtmlHandler htmlHandler = (ViewerHtmlHandler) handler;

        //Get document pages in image form
        List<PageHtml> pages = htmlHandler.getPages(DocumentName, options);

        return getHtmlInfos(pages);
        //ExEnd:RenderAsImageWithRotationTransformation
    }

    /**
     * document in html representation and reorder a page
     * @param DocumentName      file/document name
     * @param CurrentPageNumber Page existing order number
     * @param NewPageNumber     Page new order number
     * @param DocumentPassword  Password Parameter is optional
     * @return the list
     * @throws Exception the exception
     */
    public static List<HtmlInfo> renderDocumentAsHtml(String DocumentName, int CurrentPageNumber, int NewPageNumber, String DocumentPassword) throws Exception {
        // Guid implies that unique document name
        // Cast ViewerHtmlHandler class object to its base class(ViewerHandler).
        ViewerHandler handler = new ViewerHtmlHandler(config);
        //Instantiate the HtmlOptions object with setting of Reorder Transformation
        HtmlOptions options = new HtmlOptions();
        options.setTransformations(Transformation.Reorder);

        //to get html representations of pages with embedded resources
        options.setResourcesEmbedded(true);

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Call ReorderPage and pass the reference of ViewerHandler's class  parameter by reference.
        Utilities.PageTransformations.reorderPage(handler, DocumentName, CurrentPageNumber, NewPageNumber);

        //down cast the handler(ViewerHandler) to viewerHtmlHandler
        ViewerHtmlHandler htmlHandler = (ViewerHtmlHandler) handler;

        //Get document pages in html form
        List<PageHtml> pages = htmlHandler.getPages(DocumentName, options);

        return getHtmlInfos(pages);
        //ExEnd:RenderAsHtmlAndReorderPage
    }

    private static List<HtmlInfo> getHtmlInfos(List<PageHtml> pages) {
        List<HtmlInfo> contents = new ArrayList<HtmlInfo>();

        for (PageHtml page : pages) {
            HtmlInfo htmlInfo = new HtmlInfo();
            htmlInfo.setHtmlContent(page.getHtmlContent());
            htmlInfo.setPageNmber(page.getPageNumber());
            contents.add(htmlInfo);
        }
        return contents;
    }

    /**
     * Render a document in html representation whom located at web/remote location.
     * @param DocumentURL      URL of the document
     * @param DocumentPassword Password Parameter is optional
     * @throws Exception the exception
     */
    public static void renderDocumentAsHtml(URI DocumentURL, String DocumentPassword) throws Exception {
        //ExStart:RenderRemoteDocAsHtml
        //Get Configurations


        // Create html handler
        ViewerHtmlHandler htmlHandler = new ViewerHtmlHandler(config);

        //Instantiate the HtmlOptions object
        HtmlOptions options = new HtmlOptions();

        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Get document pages in html form
        List<PageHtml> pages = htmlHandler.getPages(DocumentURL, options);

        for (PageHtml page : pages) {
            //Save each page at disk
            Utilities.saveAsHtml(page.getPageNumber() + "_" + FilenameUtils.getName(DocumentURL.getPath()), page.getHtmlContent());
        }
        //ExEnd:RenderRemoteDocAsHtml
    }

    /**
     * Render simple document in image representation
     * @param documentName     File name
     * @param DocumentPassword Optional
     * @return the list
     */
    public static List<ImageInfo> renderDocumentAsImages(String documentName, String DocumentPassword) {
        //ExStart:RenderAsImage
        //Get Configurations


        // Create image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);

        //Initialize ImageOptions Object
        ImageOptions options = new ImageOptions();

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Get document pages in image form
        List<PageImage> Images = imageHandler.getPages(documentName, options);

        List<ImageInfo> contents = new ArrayList<ImageInfo>();

        for (PageImage image : Images) {
            String imgname = image.getPageNumber() + "_" + FilenameUtils.getName(documentName);
            imgname = imgname.replace("\\s+", "_");

            Utilities.saveAsImage(config.getTempPath(), imgname, image.getStream());

            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageUrl("/Uploads/images/" + FilenameUtils.getBaseName(imgname) + ".jpg?" + UUID.randomUUID().toString());
            imageInfo.setPageNmber(image.getPageNumber());
            imageInfo.setHtmlContent("<div class='image_page'><img src='" + imageInfo.getImageUrl() + "' /></div>");
            contents.add(imageInfo);
        }

        return contents;
        //ExEnd:RenderAsImage

    }

    /**
     * Render document in image representation with watermark
     * @param DocumentName     file/document name
     * @param WatermarkText    watermark text
     * @param WatermarkColor   System.Drawing.Color
     * @param WatermarkWidth   width of watermark as integer. it is optional Parameter default value is 100
     * @param DocumentPassword Password Parameter is optional
     * @return the list
     */
    public static List<ImageInfo> renderDocumentAsImages(String DocumentName, String WatermarkText, Color WatermarkColor, int WatermarkWidth, String DocumentPassword) {
        // Guid implies that unique document name
        // Create image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);
        //Initialize ImageOptions Object
        ImageOptions options = new ImageOptions();

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        // Call AddWatermark and pass the reference of ImageOptions object as 1st parameter
        Utilities.PageTransformations.addWatermark(options, WatermarkText, WatermarkColor, WatermarkPosition.Diagonal, WatermarkWidth);

        //Get document pages in image form
        List<PageImage> images = imageHandler.getPages(DocumentName, options);

        return getImageInfos(DocumentName, images);
        //ExEnd:RenderAsImageWithWaterMark
    }

    private static List<ImageInfo> getImageInfos(String DocumentName, List<PageImage> images) {
        List<ImageInfo> contents = new ArrayList<ImageInfo>();

        for (PageImage image : images) {
            String imgname = image.getPageNumber() + "_" + FilenameUtils.getBaseName(DocumentName);
            imgname = imgname.replace("\\s+", "_");

            Utilities.saveAsImage(config.getTempPath(), imgname, image.getStream());

            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageUrl("/Uploads/images/" + imgname + ".jpg?" + UUID.randomUUID().toString());
            imageInfo.setPageNmber(image.getPageNumber());
            imageInfo.setHtmlContent("<div class='image_page'><img src='" + imageInfo.getImageUrl() + "' /></div>");
            contents.add(imageInfo);
        }
        return contents;
    }

    /**
     * Render the document in image form and set the rotation angle to rotate the page while display.
     * @param DocumentName     the document name
     * @param pageNumber       the page number
     * @param RotationAngle    rotation angle in digits
     * @param DocumentPassword the document password
     * @return the list
     * @throws Exception the exception
     */
    public static List<ImageInfo> rotateDocumentAsImages(String DocumentName, int pageNumber, int RotationAngle, String DocumentPassword) throws Exception {
        // Guid implies that unique document name
        // Create image handler
        ViewerHandler handler = new ViewerImageHandler(config);
        //Initialize ImageOptions Object and setting Rotate Transformation
        ImageOptions options = new ImageOptions();
        options.setTransformations(Transformation.Rotate);

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Call RotatePages to apply rotate transformation to a page
        Utilities.PageTransformations.rotatePages(handler, DocumentName, pageNumber, RotationAngle);

        //down cast the handler(ViewerHandler) to viewerHtmlHandler
        ViewerImageHandler imageHandler = (ViewerImageHandler) handler;

        //Get document pages in image form
        List<PageImage> Images = imageHandler.getPages(DocumentName, options);

        return getImageInfos(DocumentName, Images);
    }

    /**
     * document in image representation and reorder a page
     * @param DocumentName      file/document name
     * @param CurrentPageNumber Page existing order number
     * @param NewPageNumber     Page new order number
     * @param DocumentPassword  Password Parameter is optional
     * @return the list
     * @throws Exception the exception
     */
    public static List<ImageInfo> renderDocumentAsImages(String DocumentName, int CurrentPageNumber, int NewPageNumber, String DocumentPassword) throws Exception {
        // Guid implies that unique document name
        // Cast ViewerHtmlHandler class object to its base class(ViewerHandler).
        ViewerHandler handler = new ViewerImageHandler(config);
        //Initialize ImageOptions Object and setting Reorder Transformation
        ImageOptions options = new ImageOptions();
        options.setTransformations(Transformation.Reorder);

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Call ReorderPage and pass the reference of ViewerHandler's class  parameter by reference.
        Utilities.PageTransformations.reorderPage(handler, DocumentName, CurrentPageNumber, NewPageNumber);

        //down cast the handler(ViewerHandler) to viewerHtmlHandler
        ViewerImageHandler imageHandler = (ViewerImageHandler) handler;

        //Get document pages in image form
        List<PageImage> images = imageHandler.getPages(DocumentName, options);

        return getImageInfos(DocumentName, images);
        //ExEnd:RenderAsImageAndReorderPage
    }

    /**
     * Render a document in image representation whom located at web/remote location.
     * @param DocumentURL      URL of the document
     * @param DocumentPassword Password Parameter is optional
     */
    public static void renderDocumentAsImages(URI DocumentURL, String DocumentPassword) {
        // Create image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);

        //Initialize ImageOptions Object
        ImageOptions options = new ImageOptions();

        // Set password if document is password protected.
        if (DocumentPassword != null && !DocumentPassword.isEmpty()) {
            options.setPassword(DocumentPassword);
        }

        //Get document pages in image form
        List<PageImage> Images = imageHandler.getPages(DocumentURL, options);

//        for (PageImage image : Images) {
            //Save each image at disk
            // Utilities.SaveAsImage(image.getPageNumber() + "_" + Path.GetFileName(DocumentURL.LocalPath), image.Stream);
//        }
    }

    /**
     * Render a document as it is (original form)
     * @param DocumentName the document name
     */
    public static void renderDocumentAsOriginal(String DocumentName) {
        //ExStart:RenderOriginal
        // Create image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);

        // Guid implies that unique document name

        // Get original file
        FileContainer container = imageHandler.getFile(DocumentName);

        //Save each image at disk
        // Utilities.SaveAsImage(DocumentName, container.Stream);
        //ExEnd:RenderOriginal

    }

    /**
     * Render a document in PDF Form
     * @param DocumentName the document name
     */
    public static void renderDocumentAsPDF(String DocumentName) {
        //ExStart:RenderAsPdf
        // Create/initialize image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);

        //Initialize PdfFileOptions object
        PdfFileOptions options = new PdfFileOptions();

        // Guid implies that unique document name
        options.setGuid(DocumentName);

        // Call GetPdfFile to get FileContainer type object which contains the stream of pdf file.
        FileContainer container = imageHandler.getPdfFile(options);

        //Change the extension of the file and assign to a String type variable filename
        String filename = FilenameUtils.getBaseName(DocumentName) + ".pdf";

        //Save each image at disk
        Utilities.saveFile(filename, container.getStream());
        //ExEnd:RenderAsPdf

    }

    /**
     * Load directory structure as file tree
     * @param Path the path
     */
    public static void loadFileTree(String Path) {
        //ExStart:LoadFileTree
        // Create/initialize image handler
        ViewerImageHandler imageHandler = new ViewerImageHandler(config);

        // Load file tree list for custom path
        FileTreeOptions options = new FileTreeOptions(Path);

        // Load file tree list for ViewerConfig.StoragePath
        FileTreeContainer container = imageHandler.loadFileTree(options);

        for (FileDescription node : container.getFileTree()) {
            if (node.isDirectory()) {
                System.out.println(String.format(
                        "Guid: %s | Name: %s | LastModificationDate: %s",
                        node.getGuid(),
                        node.getName(),
                        node.getLastModificationDate()
                ));
            } else {
                System.out.println(String.format(
                        "Guid: %s | Name: %s | Document type: %s | File type: %s | Extension: %s | Size: %s | LastModificationDate: %s",
                        node.getGuid(),
                        node.getName(),
                        node.getDocumentType(),
                        node.getFileType(),
                        node.getExtension(),
                        node.getSize(),
                        node.getLastModificationDate()
                ));
            }
        }
    }

    /**
     * Load page image byte [ ].
     * @param filename the filename
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] loadPageImage(String filename) throws IOException {
        final File imagePath = Utilities.makeImagePath(config.getTempPath(), filename);
        return FileUtils.readFileToByteArray(imagePath);
    }
}
