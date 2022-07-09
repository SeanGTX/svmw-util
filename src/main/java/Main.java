import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Main {

    private static final String helpMsg =
            "svmw-util v0.1 by megboyzz\n"
                    + "CLI Утилита предназачена для создания svmw для DevMenu NFSMW Android\n\n"
                    + "использование: \n"
                    + "Создание svmw: svmw-util -c <описание файла> -p <путь до какого-либо сохранения nfstr_save.sb> -s <Путь куда сохранить>\n"
                    + "Открыть svmw: svmw-util -v <путь до *.svmw>\n"
                    + "Помощь: svmw-util -h\n";

    private static final String createKey = "-c";

    private static final String pathKey = "-p";

    private static final String helpKey = "-h";

    private static final String saveKey = "-s";

    private static final String viewKey = "-v";

    private static final String badSyntax = "Неверные ключи\n";

    private static final String noInput = "Пустой вход, выход...";

    private static final String errorSb = "%s - не файл сохранения! Выход...";

    private static final String errorSvmw = "%s - не svmw! Выход...";

    private static final String description = "Описание: %s\n";

    private static final String dateOfCreation = "Дата создания: %s\n";

    private static final String fileNotFound = "Файл не найден: %s";

    private static final String success = "Успешно!";

    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println(noInput);
            System.exit(0);
        }


        switch (args[0]) {
            case createKey: {
                File sb = new File(args[3]);
                File saveTo = new File(args[5]);

                if (!sb.exists()) {
                    System.out.printf(fileNotFound, args[3]);
                    System.exit(0);
                }
                if (!saveTo.exists()) {
                    try {
                        saveTo.createNewFile();
                    } catch (IOException e) {
                    }
                }

                SaveManager manager = new SaveManager();
                manager.createBundleFile(args[1], saveTo, sb);

                System.out.println(success);

                break;
            }
            case viewKey: {
                File svmw = new File(args[1]);
                if (!svmw.exists()) {
                    System.out.printf(fileNotFound, args[1]);
                    System.exit(0);
                }
                SaveManager manager = new SaveManager();
                String date = manager.getFormattedDate(svmw);
                String des = manager.getDescriptionOf(svmw);

                System.out.printf(description, des);
                System.out.printf(dateOfCreation, date);

                break;
            }
            case helpKey:
                System.out.println(helpMsg);
                break;
            default:
                System.out.println(badSyntax);
                break;
        }

    }

}
