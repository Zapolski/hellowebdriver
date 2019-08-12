import java.util.ArrayList;
import java.util.Arrays;

public class Test {

    public static String expected = "We've  been of waiting of ages.";
    public static String actual = "We've aasdf asas.";

    public static void main(String[] args) {

        // [$&+,:;=?@#|'<>.-^*()%!]
        // переводим в ловеркейс, удаляем знаки препинания, количество пробелов между словами сводим к одному

        // заменить дефис на двоеточие
        //alert( '12-34-56'.replace( /-/g, ":" ) )  // 12:34:56


            expected = expected.replaceAll("[,:;?.!]","").replaceAll("[ ]{2,}"," ");
            actual = actual.replaceAll("[,:;?.!]","").replaceAll("[ ]{2,}"," ");;

        ArrayList<String> expectedList = new ArrayList<String>(Arrays.asList(expected.split(" ")));
        ArrayList<String> actualList = new ArrayList<String>(Arrays.asList(actual.split(" ")));
        ArrayList<String> resultList = new ArrayList<>();

        int expIndex = 0;
        int actIndex = 0;
        String expStr;

        while(!actualList.isEmpty() && !expectedList.isEmpty()){

            if (expIndex<expectedList.size()){
                expStr = expectedList.get(expIndex);
            }else{
                expStr="";
            }
            String actStr = actualList.get(actIndex);

            if (actStr.equals(expStr)) {
                resultList.add(actStr+"(+)");//правильно написано, на своем месте
                expectedList.remove(expIndex);
            }else{
                if (expectedList.contains(actStr)){
                    resultList.add(actStr+"(+/-)");//присутствует в предложении (правильно написано), но не на своем месте
                    expectedList.remove(actStr);
                }else{
                    resultList.add(actStr+"(-)");//не присутствет в предложении (неправильно написно)
                    expIndex++;
                }
            }
            actualList.remove(actIndex);
        }

        // дополняем результат, если во введенной строке больше слов, чем в исходной
        for (String str : actualList){
            resultList.add(str+"(-)");
        }

        System.out.println("Source: "+expectedList);
        System.out.println("Actual: "+actualList);
        System.out.println("Result: "+resultList);

        System.out.println(expected);
        System.out.println(actual);
    }
}
