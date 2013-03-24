import java.io.*;


public class filelist implements Serializable{
	public String filename[];
	public long time[];


	//lists the files in a directory
	public filelist(File dir){
		File f[] = dir.listFiles();
		filename = new String[f.length];
		time = new long[f.length];
		for(int k=0;k<f.length;k++){
			filename[k]	= f[k].getName();
			time[k] = f[k].lastModified();
		} 

	}

	//retunrs if this file list has a file with a given name and modified date	
	public boolean hasfile(String name,long t){
		for(int k=0;k<filename.length;k++){
			if(filename[k].equals(name) && time[k]==t){
				return true ;
			}
		}
		return false ;
	}

	//retunrs if this file list has a file with a given name 
	public boolean hasfile(String name){
		for(int k=0;k<filename.length;k++){
			if(filename[k].equals(name)){
				return true ;
			}
		}
		return false ;
	}

	//lists the files in the first list that are not in the second list
	public filelist(filelist a, filelist b){
		String fl[]=new String[a.filename.length];
		long t[]=new long[a.filename.length];
		int k=0;//k keeps track of how many are actually in the list
		for(int j=0;j<t.length;j++){//for each file in a
			if(!b.hasfile(a.filename[j],a.time[j])){//if not in b
				fl[k] = a.filename[j] ;//add to new list
				t[k] = a.time[j] ;
				k++ ;
			}
		}
		//instantiate actual object arrays to correct length
		filename = new String[k];
		time = new long[k] ;
		for(int j=0;j<k;j++){//move data into actual arrays
			filename[j] = fl[j] ;
			time[j] = t[j] ;
		}


	}

	public String toString(){
		String s = "";
		for(int k=0;k<filename.length;k++){
			s+=filename[k]+"--"+time[k]+"\n";

		}
		return s ;
	}



	public static void main(String args[]){
		filelist f = new filelist(new File(".")) ;
		System.out.println(f);
		String s = f.filename[0] ;
		String q[] = s.split("\\.");
		System.out.println(q[1]);
	}


}