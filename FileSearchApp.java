public class FileSearchApp {
    private String path;
    private String regex;
    private String zipFileName;
    private Pattern pattern;
    private final ArrayList<File> zipFiles = new ArrayList();
    
    public static void main(String[] args) {
        FileSearchApp app = new FileSearchApp();
        
        switch( Math.min( args.length , 3)){
            case 0:
                System.out.println("USAGE: FileSearchApp path [regex] [zipfile]");
                return;
            case 3: app.setZipFileName(args[2]);
            case 2: app.setRegex(args[1]);
            case 1: app.setPath(args[0]);
        }
        try{
            app.walkDirectory(app.getPath());
        }
        catch( Exception e){
            e.printStackTrace();
        }
        
    }

    public void walkDirectory(String path){

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public void processFile( File file){
        System.out.println("Process file:" + file);
        try {    
            if( searchFile( file)){
                addFileToZip(file);
            }
        } catch (IOException | UncheckedIOException ex) {
            System.out.println( "Error processing file:" + file + ":" + ex);
        }
    }
	
	public boolean searchFileJava8( File file ) throws IOException {
        return (! file.isDirectory())? Files.lines( file.toPath() , StandardCharsets.UTF_8)
                                        .anyMatch( t->searchText( t )): false;
    }
	
	//-------------- zipFiles   Implementations ---------------------------------
    public void zipFilesJava7() throws IOException{
        try( ZipOutputStream out = 
                new ZipOutputStream( new FileOutputStream( getZipFileName()))){
            File baseDir = new File( getPath());
            
            for( File file : zipFiles){
                String fileName = getRelativeFileName(file, baseDir);
                
                ZipEntry zipEntry = new ZipEntry( fileName);
                zipEntry.setTime( file.lastModified());
                out.putNextEntry(zipEntry);
                
                Files.copy(file.toPath(), out);
                out.closeEntry();
            }
        }
    }
	
    public void zipFilesJava6() throws IOException {
        ZipOutputStream out = null;
        
        try{
            out = new ZipOutputStream( new FileOutputStream( getZipFileName() ));
            File baseDir = new File( getPath() );
            for( File file : zipFiles ){
                String fileName = getRelativeFileName( file, baseDir);
                
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime( file.lastModified() );
                out.putNextEntry( zipEntry );
                
                int bufferSize = 2048;
                byte [] buffer = new byte[bufferSize];
                int len = 0;
                
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(file), bufferSize
                );
                
                while( (len = in.read(buffer, 0, bufferSize)) != -1 ){
                    out.write( buffer, 0, len);
                }
                
                in.close();
                out.closeEntry();
            } //for
            
        }
        finally{
                out.close();
            }
    }
}