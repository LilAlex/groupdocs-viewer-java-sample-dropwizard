package com.groupdocs.viewer.samples.dropwizard.resources;

import com.groupdocs.viewer.samples.dropwizard.config.DropwizardConfig;
import com.groupdocs.viewer.samples.dropwizard.config.ViewerConfig;
import com.groupdocs.viewer.samples.dropwizard.model.Utilities;
import com.groupdocs.viewer.samples.dropwizard.model.ViewGenerator;
import com.groupdocs.viewer.samples.dropwizard.model.business.HtmlInfo;
import com.groupdocs.viewer.samples.dropwizard.model.business.ImageInfo;
import com.groupdocs.viewer.samples.dropwizard.views.ViewerView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The type Viewer resource.
 * @author Aleksey Permyakov
 */
@Path("/")
public class ViewerResource {
    private final DropwizardConfig dropwizardConfig;

    /**
     * Instantiates a new Viewer resource.
     * @param viewerConfig     the viewer config
     * @param dropwizardConfig the dropwizard config
     */
    public ViewerResource(ViewerConfig viewerConfig, DropwizardConfig dropwizardConfig) {
        ViewGenerator.initGenerator(viewerConfig);
        this.dropwizardConfig = dropwizardConfig;
    }

    /**
     * Gets index.
     * @param request the request
     * @return the index
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public ViewerView getIndex(@Context HttpServletRequest request) {
        return new ViewerView(request.getContextPath());
    }

    /**
     * Upload handler string.
     * @param formDataMultiPart the form data multi part
     * @param request           the request
     * @param response          the response
     * @return the string
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/Controllers/UploadHandler.ashx")
    public String uploadHandler(FormDataMultiPart formDataMultiPart, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            Map<String, List<FormDataBodyPart>> fieldsByName = formDataMultiPart.getFields();
            for (List<FormDataBodyPart> fields : fieldsByName.values()) {
                for (FormDataBodyPart field : fields) {
                    InputStream inputStream = field.getEntityAs(InputStream.class);
                    String fileName = field.getName();
                    if (inputStream != null && fileName != null) {
                        //Check the document type here
                        if (Utilities.checkExtenstion(FilenameUtils.getExtension(fileName))) {
                            // Save the posted file
                            final File outFile = new File(Utilities.getUploadPath(dropwizardConfig) + fileName);
                            FileUtils.writeByteArrayToFile(outFile, IOUtils.toByteArray(inputStream));
                            //write the file path of successfully saved file.
                            return fileName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Generate the response with error code
        response.setStatus(422);
        return "Please upload a valid MS Word file";
    }

    /**
     * Main handler response.
     * @param filename the filename
     * @return the response
     */
    @GET
    @Produces("image/png")
    @Path("/Uploads/images/{filename}")
    public Response mainHandler(@PathParam("filename") String filename) {
        try {
            final byte[] bytes = ViewGenerator.loadPageImage(filename);
            return Response.ok().entity(bytes).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }

    /**
     * Main handler response.
     * @param request the request
     * @return the response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Controllers/MainHandler.ashx")
    public Response mainHandler(@Context HttpServletRequest request) {
        try {
            final String filePath = request.getParameter("filepath");
            // Check the action variable in ajax http request
            if ("renderashtml".equals(request.getParameter("action"))) {
                // File path is also included in the http request

                List<HtmlInfo> lstPages = ViewGenerator.renderDocumentAsHtml(filePath, null);
                return generateResponse(lstPages);

            } else if ("renderashtmlwithwatermark".equals(request.getParameter("action"))) {

                String WatermarkText = request.getParameter("watermark");
                List<HtmlInfo> lstPages = ViewGenerator.renderDocumentAsHtml(filePath, WatermarkText, Color.RED, 100, null);
                return generateResponse(lstPages);
            } else if ("renderashtmlwithreorder".equals(request.getParameter("action"))) {

                int startIndex = Integer.parseInt(request.getParameter("start"));
                int newIndex = Integer.parseInt(request.getParameter("new"));
                List<HtmlInfo> lstPages = ViewGenerator.renderDocumentAsHtml(filePath, startIndex, newIndex + 1, null);
                return generateResponse(lstPages);
            } else if ("renderashtmlwithrotate".equals(request.getParameter("action"))) {

                int pageId = Integer.parseInt(request.getParameter("page"));
                int angle = Integer.parseInt(request.getParameter("angle"));
                List<HtmlInfo> lstPages = ViewGenerator.rotateDocumentAsHtml(filePath, pageId, angle, null);
                return generateResponse(lstPages);
            }
            if ("renderasimage".equals(request.getParameter("action"))) {
                // File path is also included in the http request

                List<ImageInfo> lstPages = ViewGenerator.renderDocumentAsImages(filePath, null);
                return generateResponse(lstPages);

            } else if ("renderasimagewithwatermark".equals(request.getParameter("action"))) {

                String WatermarkText = request.getParameter("watermark");
                List<ImageInfo> lstPages = ViewGenerator.renderDocumentAsImages(filePath, WatermarkText, Color.RED, 100, null);
                return generateResponse(lstPages);
            } else if ("renderasimagewithreorder".equals(request.getParameter("action"))) {

                int startIndex = Integer.parseInt(request.getParameter("start"));
                int newIndex = Integer.parseInt(request.getParameter("new"));
                List<ImageInfo> lstPages = ViewGenerator.renderDocumentAsImages(filePath, startIndex, newIndex + 1, null);
                return generateResponse(lstPages);
            } else if ("renderasimagewithrotate".equals(request.getParameter("action"))) {

                int pageId = Integer.parseInt(request.getParameter("page"));
                int angle = Integer.parseInt(request.getParameter("angle"));
                List<ImageInfo> lstPages = ViewGenerator.rotateDocumentAsImages(filePath, pageId, angle, null);
                return generateResponse(lstPages);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }


    /**
     * Generate response response.
     * @param obj the obj
     * @return the response
     */
    public Response generateResponse(Object obj) {
        return Response.ok(200).entity(obj).build();
    }
//    public void GenerateFile(Stream obj, HttpContext context)
//    {
//        context.Response.Clear();
//        context.Response.ContentType = "application/pdf";
//        // context.Response.AddHeader("Content-Disposition", "attachment; filename=" + fileName);
//        // context.Response.BinaryWrite(;
//        context.Response.End();
//    }
}
