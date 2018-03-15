package tv.sterk.uploader;

import net.bican.wordpress.*;
import net.bican.wordpress.exceptions.FileUploadException;
import net.bican.wordpress.exceptions.InsufficientRightsException;
import net.bican.wordpress.exceptions.InvalidArgumentsException;
import net.bican.wordpress.exceptions.ObjectNotFoundException;
import redstone.xmlrpc.XmlRpcFault;

import net.bican.wordpress.Post;

import java.io.*;
import java.util.Arrays;

public class WPPoster {

    static Wordpress wp;

    String localVideoDirectory = PropertyReader.properties.getProperty("localVideoDirectory") + "\\";
    String tmpVideoDirectory = PropertyReader.properties.getProperty("tmpVideoDirectory") + "\\";

    public WPPoster(String fileNameOnFTP) throws XmlRpcFault, InvalidArgumentsException, ObjectNotFoundException, InsufficientRightsException, IOException, FileUploadException {

        wp = new Wordpress(PropertyReader.properties.getProperty("wpUsername"), PropertyReader.properties.getProperty("wpPass"), PropertyReader.properties.getProperty("wpHost"));
//        FilterPost filter = new FilterPost();
//        filter.setNumber(10);
//        List<Post> recentPosts = wp.getPosts(filter);
//        System.out.println("Here are the ten recent posts:");
//        for (Post page : recentPosts) {
//            System.out.println(page.getPost_id() + ":" + page.getPost_title());
//        }
//        FilterPost filter2 = new FilterPost();
//        filter2.setPost_type("page");
//        wp.getPosts(filter2);
//        List<Post> pages = wp.getPosts(filter2);
//        System.out.println("Here are the pages:");
//        for (Post pageDefinition : pages) {
//            System.out.println(pageDefinition.getPost_title());
//        }
        Post recentPost = new Post();
        File file = new File(tmpVideoDirectory + "\\" + fileNameOnFTP.replace(".mp4", ".png").replace(".mpg", ".png").replace(".avi", ".png"));
        final Term term1 = wp.getTerm("category", Integer.valueOf(fileNameOnFTP.substring(0, 3)));

        try (InputStream media = new FileInputStream(file)) {

            String imageFileNameWithPath = file.getAbsolutePath();
            String imageFileName = fileNameOnFTP.replace(".mp4", ".jpg").replace(".mpg", ".jpg").replace(".avi", ".jpg");

            MediaItemUploadResult mediaUploaded = wp.uploadFile(media, imageFileName);
            MediaItem r = wp.getMediaItem(mediaUploaded.getId());

            recentPost.setTerms(Arrays.asList(new Term[]{term1}));
            recentPost.setPost_title(fileNameOnFTP.replace("_", " ").replace(".mp4", "").replace(".mpg", "").substring(4));
            recentPost.setPost_content("[clappr media=\"/video/" + fileNameOnFTP.replace(".mpg", ".mp4").replace(".avi", ".mp4") + "\" type=\"video/mp4\" autoplay=\"yes\"]");
            recentPost.setPost_status("publish");
            recentPost.setPost_format("Video");
            recentPost.setPost_thumbnail(r);

            Integer result = wp.newPost(recentPost);
        }
    }
}