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
    }
    
    public void walkDirectoryJava( String path ) throws IOException {
        Files.walk( Paths.get(path)).forEach( f->processFile( f.toFile()));
    }
}