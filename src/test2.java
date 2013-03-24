import java.awt.image.BufferedImage;
import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class test2 {

	/** Name to store the users access token */
	private static final String ACCESS_TOKEN = "";
	/** Name to store the users access token secret */
	private static final String ACCESS_TOKEN_SECRET = "";
	/** Consumer Key generated when you registered your app at https://dev.twitter.com/apps/ */
	private static final String CONSUMER_KEY = "";
	/** Consumer Secret generated when you registered your app at https://dev.twitter.com/apps/  */
	private static final String CONSUMER_SECRET = ""; // XXX Encode in your app
	/** The url that Twitter will redirect to after a user log's in - this will be picked up by your app manifest and redirected into this activity */

	private static final String folder = "D:/Media/Pictures/My Little Pony" ;

	static long lastposttime = 0 ;
	static long timebetweenposts = 3600000 ; // 1 hour


	public static void main(String args[]){

		while(true){
			if((System.currentTimeMillis() - lastposttime ) > timebetweenposts){
				try{
					postrandompicture() ;
					lastposttime = System.currentTimeMillis() ;
				}catch(Exception e){
					System.err.println(e) ;
					e.printStackTrace() ;
				}
			}
			try{
				Thread.sleep(60000) ;
			}catch(Exception e){

			}
		}
	}


	public static void postrandompicture() throws Exception{


		//get file list
		filelist files = new filelist(new File(folder)) ;
		String filename = files.filename[(int)(Math.random()*files.filename.length)] ;
		String e[] = filename.split("\\.") ;
		String ext = e[e.length-1].toLowerCase() ;
		//check file type
		
		System.out.println(filename);
		if(! ( ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png") || ext.equals("bmp"))){
			throw new Exception ("Invalid file type!") ;
		}
		//String filename ="periodic_table_of_the_elements_of_harmony_by_metalgearsamus-d52njgx.png" ;
		String filepath = folder +"/" + filename;
		File file = new File(filepath) ;
		long size = file.length() ;
		
		System.out.println("size:" + size) ;




		if(size > 3000000){ // if file is too big
			System.out.println("Image too large. Create jpeg temp file.") ;
			BufferedImage image = imageutil.loadimage(filepath);
			file = new File("./temp.jpg") ;//create temp file
			imageutil.saveImage(image,file,95) ;//save as jpeg
			size = file.length() ;
			System.out.println("new size:" + size) ;

			while(size > 3000000){
				image = imageutil.convertimage(imageutil.halfsize(imageutil.convertimage(image))) ;
				imageutil.saveImage(image,file,95) ;//save as jpeg
				size = file.length() ;
				System.out.println("new size:" + size) ;
			}

		}



		//Get Twitter
		Twitter twitter = new TwitterFactory().getInstance();
		//Tell Twitter we want to use it with our app
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		// Create the twitter access token from the credentials hardcoded at the top of the file
		AccessToken at = new AccessToken( ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(at);


		uploadPic(file, "#mlpfim " + filename, twitter) ;

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

	public static  void uploadPic(File file, String message,Twitter twitter) throws Exception  {

		StatusUpdate status = new StatusUpdate(message);
		status.setMedia(file);
		twitter.updateStatus(status);

	}

}
