import java.io.File;

public class TestForEmptyCatalog {
    public static void main(String[] args) {
        String catalog = "d:\\test\\English\\words";

        File directory = new File(catalog);
        for (File currentDir: directory.listFiles()){
            if (currentDir.isDirectory() && currentDir.listFiles().length == 0){
                currentDir.delete();
                System.out.println("Empty catalog: ["+ currentDir.getName()+"] is removed");
            }
        }
    }
}
