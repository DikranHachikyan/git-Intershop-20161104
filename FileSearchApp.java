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
	
	/**
	* Intershop search files.
	*/
    public boolean searchFileJava( File file) throws FileNotFoundException{
        
        return (! file.isDirectory())? Files.lines( file.toPath() , StandardCharsets.UTF_8)
                                        .anyMatch( t->searchText( t )): false;    
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
	
	//walk directories - functionalities
	public void walkDirectory( String path) throws IOException {
        walkDirectoryJava(path);
        zipFilesJava();
    }

    public void walkDirectoryJava( String path ) throws IOException {
        Files.walk( Paths.get(path)).forEach( f->processFile( f.toFile()));
    }

    private boolean searchText(String text) {
        System.out.println("Search Line:" + text);
        return ( this.getRegex() == null )? true :
                                            //text.toLowerCase().contains( this.getRegex().toLowerCase() );
                                            //text.matches(this.getRegex());
                                            this.pattern.matcher(text).find();
    }

    private String getRelativeFileName(File file, File baseDir) {
        String fileName = file.getAbsolutePath().substring( baseDir.getAbsolutePath().length());
        
        fileName = fileName.replace("\\", "/");
        
        while( fileName.startsWith("/")){
            fileName = fileName.substring(1);
        }
        return fileName;
    }
	
	public void zipFilesJava() throws IOException{
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
}