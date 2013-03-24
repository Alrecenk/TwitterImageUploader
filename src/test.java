import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class test {

	/** Name to store the users access token */
    private static final String ACCESS_TOKEN = "";
    /** Name to store the users access token secret */
    private static final String ACCESS_TOKEN_SECRET = "";
    /** Consumer Key generated when you registered your app at https://dev.twitter.com/apps/ */
    private static final String CONSUMER_KEY = "";
    /** Consumer Secret generated when you registered your app at https://dev.twitter.com/apps/  */
    private static final String CONSUMER_SECRET = ""; // XXX Encode in your app
    /** The url that Twitter will redirect to after a user log's in - this will be picked up by your app manifest and redirected into this activity */
    
	
	
	public static void main(String args[]){
		//Get Twitter
		Twitter twitter = new TwitterFactory().getInstance();
		//Tell Twitter we want to use it with our app
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		 // Create the twitter access token from the credentials hardcoded at the top of the file
        AccessToken at = new AccessToken( ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        twitter.setOAuthAccessToken(at);
        
        
		
		uploadPic(new File("./DerpyTestImage.png"), "DerpyTestImage.png", twitter) ;
		
		System.out.println("Operation completed!") ;
		
	}
	
	/**
	 * To upload a picture with some piece of text.
	 * 
	 * 
	 * @param file The file which we want to share with our tweet
	 * @param message Message to display with picture
	 * @param twitter Instance of authorized Twitter class
	 * @throws Exception exception if any
	 */

	public static  void uploadPic(File file, String message,Twitter twitter) {
	    try{
	        StatusUpdate status = new StatusUpdate(message);
	        status.setMedia(file);
	        twitter.updateStatus(status);}
	    catch(TwitterException e){
	        //Log.d("TAG", "Pic Upload error" + e.getErrorMessage());
	    	System.err.println("Pic Upload error" + e.getErrorMessage()) ;
	    }catch(Exception e){
	    	System.err.println(e) ;
	    	e.printStackTrace() ;
	    }
	}
	
}
