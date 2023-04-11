import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;

import static java.awt.event.KeyEvent.VK_TAB;


public class FileManager extends JFrame implements ActionListener, KeyListener {
    // 当前目录路径
    private static String currentPath;
    // 新建静态变量
    private static JTextArea textArea;
    private static JTextField textField;

    public FileManager() {
        super("文件管理器");
        //表示当用户关闭窗口时，程序将自动退出。
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口大小
        setSize(800, 600);
        //表示将窗口设置在屏幕中央显示。
        setLocationRelativeTo(null);
        //设置窗口的布局管理器为BorderLayout，这意味着窗口将被分为5个区域：北、南、东、西和中心。
        setLayout(new BorderLayout());

        //创建一个JTextArea对象，设置其为不可编辑状态
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        //将JScrollPane添加到窗口的中心区域，以便用户可以在其中查看文件系统的输出。
        add(scrollPane, BorderLayout.CENTER);

        //创建一个JPanel对象，设置其布局管理器为BorderLayout。
        //然后创建一个JTextField对象，并将其添加到JPanel的中心区域。
        //最后将JPanel添加到窗口的南部区域，以便用户可以在其中输入命令。
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        textField = new JTextField();
        //将当前对象（FileManager）注册为文本字段（textField）的动作监听器，以便在用户按下回车键时执行相应的操作。
        textField.addActionListener(this);
        //取消焦点转移
        textField.setFocusTraversalKeysEnabled(false);
        textField.addKeyListener(this);

        panel.add(textField, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        //设置为可见
        setVisible(true);

        // 初始化，设置当前目录为程序所在目录
        currentPath = System.getProperty("user.dir");
        // 打印当前目录路径和命令提示符
        textArea.append(currentPath + ">");
    }

    public static void main(String[] args) {
        new FileManager();
    }

    /**
     * 解析并执行用户命令
     */
    private static void executeCommand(String input) {
        // 将输入的命令字符串分割为命令名和参数列表
        String[] parts = input.split(" ");
        String command = parts[0];
        String[] args = new String[parts.length - 1];
        for (int i = 0; i < args.length; i++) {
            args[i] = parts[i + 1];
        }
        // 将输入的命令添加到文本区域中
        textArea.append(input + "\n");
        // 根据命令名执行相应的操作
        switch (command) {
            case "cd":
                changeDirectory(args);
                break;
            case "md":
                createDirectory(args);
                break;
            case "rd":
                deleteDirectory(args);
                break;
            case "ren":
                renameFile(args);
                break;
            case "move":
                moveFile(args);
                break;
            case "del":
                deleteFile(args);
                break;
            case "encrypt":
                encryptFile(args);
                break;
            case "decrypt":
                decryptFile(args);
                break;
            case "type":
                viewFile(args);
                break;
            case "copy":
                copyFile(args);
                break;
            case "where":
                findFile(args);
                break;
            case "help":
                showHelp(args);
                break;
            case "ll":
            case "dir":
                dirListFiles(args);
                break;
            case "ls":
                lsListFiles(args);
                break;
            case "size":
                getDirectorySizeUtil(args);
                break;
            case "time":
                lastModified(args);
                break;
            default:
                unknownCommand(args);
                break;
        }
    }

    /**
     * 改变当前目录
     *
     * @param args 命令行参数
     */
    private static void changeDirectory(String[] args) {
        // 如果参数为"..", 则返回上级目录
        if (args[0].equals("..")) {
            // 获取当前目录的父目录
            File parent = new File(currentPath).getParentFile();
            // 如果父目录存在就更新当前目录
            if (parent != null) {
                currentPath = parent.getAbsolutePath();
            }
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：  cd 文件名                  更改当前目录\n");
        } else {
            // 否则，转到指定目录
            // 创建指定目录的File对象
            File dir = new File(args[0]);
            // 如果目录存在，更新当前目录
            if (dir.isDirectory()) {
                currentPath = dir.getAbsolutePath();
            } else { // 否则，输出错误信息
                textArea.append("系统找不到指定的路径。" + "\n");
            }
        }
    }


    /**
     * 创建目录
     *
     * @param args 参数列表
     */
    private static void createDirectory(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法：md 目录" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：  md 文件名                  创建新目录\n");
        } else {
            // 在当前目录下创建新目录
            File dir = new File(currentPath, args[0]);
            if (dir.mkdir()) {
                textArea.append("目录已创建：" + dir.getAbsolutePath() + "\n");
            } else {
                textArea.append("无法创建目录。" + "\n");
            }
        }
    }

    /**
     * 删除指定目录
     *
     * @param args 命令行参数
     */
    private static void deleteDirectory(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法: rd directory" + "\n"); // 如果参数为空，输出错误信息
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：rd 文件名                  删除一个目录\\n\"");
        } else {
            // 删除指定目录
            // 创建指定目录的File对象
            File dir = new File(currentPath, args[0]);
            // 如果目录存在且删除成功
            if (dir.isDirectory() && dir.delete()) {
                // 输出删除成功信息
                textArea.append("目录已删除: " + dir.getAbsolutePath() + "\n");
            } else {
                // 输出删除失败信息
                textArea.append("未能删除目录。" + "\n");
            }
        }
    }


    /**
     * 重命名文件或目录
     *
     * @param args 命令行参数
     */
    private static void renameFile(String[] args) {
        if (args.length < 2) {
            // 如果参数不足，输出错误信息
            textArea.append("语法错误。用法: ren oldname newname" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：ren 旧文件名 新文件名       文件或者是文件夹重命名\n");
        } else {
            // 重命名文件或目录
            // 创建旧文件的File对象
            File oldFile = new File(currentPath, args[0]);
            // 创建新文件的File对象
            File newFile = new File(currentPath, args[1]);
            // 如果重命名成功
            if (oldFile.renameTo(newFile)) {
                textArea.append("文件已重命名: " + oldFile.getAbsolutePath() + " -> " + newFile.getAbsolutePath() + "\n"); // 输出重命名成功信息
            } else {
                // 输出重命名失败信息
                textArea.append("无法重命名文件。" + "\n");
            }
        }
    }


    /**
     * 复制文件
     */
    private static void copyFile(String[] args) {
        if (args.length < 2) {
            textArea.append("语法错误。用法: copy source destination" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：copy 资源带文件名的文件目录 资源带文件名的文件目录          复制文件\n");
        } else {
            // 复制文件
            Path source = Paths.get(args[0]);
            Path destination = Paths.get(args[1]);
            try {
                Files.copy(source, destination);
                textArea.append("复制的文件：" + source.toString() + " -> " + destination.toString() + "\n");
            } catch (IOException e) {
                textArea.append("未能复制文件。" + "\n");
            }
        }
    }


    /**
     * 移动文件
     */
    private static void moveFile(String[] args) {
        if (args.length < 2) {
            textArea.append("语法错误。用法: move source destination" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：move 资源带文件名的文件目录 资源带文件名的文件目录          移动文件\n");
        } else {
            // 移动文件
            Path source = Paths.get(args[0]);
            Path destination = Paths.get(args[1]);
            try {
                Files.move(source, destination);
                textArea.append("移动的文件：" + source.toString() + " -> " + destination.toString() + "\n");
            } catch (IOException e) {
                textArea.append("无法移动文件。" + "\n");
            }
        }
    }

    /**
     * 删除文件
     */
    private static void deleteFile(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法: del filename" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：del 文件名                 删除文件\n");
        } else {
            // 删除文件
            File file = new File(currentPath, args[0]);
            if (file.isFile() && file.delete()) {
                textArea.append("文件已删除: " + file.getAbsolutePath() + "\n");
            } else {
                textArea.append("未能删除文件。" + "\n");
            }
        }
    }

    /**
     * 查看文件内容
     */
    private static void viewFile(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。 type：filename" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：type 文件名                查看文件的内容\n");
        } else {
            // 查看文件内容
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(currentPath, args[0])))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
            } catch (IOException e) {
                textArea.append("无法查看文件。" + "\n");
            }
        }
    }


    /**
     * 编辑文件内容
     */
    private static void editFile(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法: edit filename" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：edit 文件名                编辑文件内容\n");
        } else {
            // 编辑文件内容
            File file = new File(currentPath, args[0]);
            if (file.isFile()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    Scanner scanner = new Scanner(System.in);
                    String line;
                    while ((line = scanner.nextLine()) != null) {
                        if (line.equals(":q")) {
                            break;
                        }
                        writer.write(line);
                        writer.newLine();
                    }
                    textArea.append("文件已保存: " + file.getAbsolutePath() + "\n");
                } catch (IOException e) {
                    textArea.append("无法编辑文件" + "\n");
                }
            } else {
                textArea.append("文件不存在。" + "\n");
            }
        }
    }

    /**
     * 列出当前目录下的文件和子目录
     */
    private static void lsListFiles(String[] args) {
        if (args.length == 0) {
            // 获取当前目录
            File dir = new File(currentPath);
            // 获取当前目录下的所有文件和子目录
            File[] files = dir.listFiles();
            // 如果当前目录下有文件或子目录
            if (files != null) {
                // 遍历所有文件和子目录
                textArea.append("文件名\t文件类型\n");
                for (File file : files) {
                    // 将文件或子目录的名称添加到文本区域中
                    textArea.append(file.getName() + (file.isDirectory() ? " \t文件夹" : "\t文件") + "\n");
                }
            } else {
                // 如果当前目录下没有文件或子目录，则在文本区域中添加一条消息
                textArea.append("无法列出文件。" + "\n");
            }
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：列出当前目录下的文件和子目录\n");
        }
    }

    private static void dirListFiles(String[] args) {
        if (args.length == 0) {
            // 获取当前目录
            File dir = new File(currentPath);
            // 获取当前目录下的所有文件和子目录
            File[] files = dir.listFiles();
            // 如果当前目录下有文件或子目录
            if (files != null) {
                textArea.append("文件类型\t\t文件名\t\t最后一次修改时间\t文件大小\n");
                // 遍历所有文件和子目录
                for (File file : files) {
                    // 如果是子目录，输出"文件夹"，否则输出"文件"
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String lastModified = simpleDateFormat.format(new Date(file.lastModified()));
                    textArea.append((file.isDirectory() ? "文件夹\t\t" : "文件\t\t") + file.getName() + "\t\t" + lastModified + "\t\t");
                    // 如果是子目录，获取子目录大小并输出
                    if (file.isDirectory()) {
                        long size = getDirectorySize(file);
                        textArea.append(size / 1024 + "MB\n");
                        // 如果是文件，获取文件大小并输出
                    } else {
                        long size = file.length();
                        textArea.append(size / 1024 + "MB\n");
                    }
                }
                // 如果当前目录下没有文件或子目录，输出"无法列出文件"
            } else {
                textArea.append("无法列出文件。\n");
            }
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：列出当前目录下的文件和子目录以及他们的文件大小以及最近修改时间\n");
        }
    }

    private static String lastModified(String[] args) {
        // 判断参数是否为空
        if (args.length == 0) {
            textArea.append("输入有误。 " + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：time 文件名                输出当前目录或文件的最近修改时间\n");
        } else {
            // 编辑文件内容
            File file = new File(currentPath, args[0]);
            // 判断文件是否存在
            if (file.exists()) {
                String name = file.getName();
                String lastModified = new Date(file.lastModified()).toString();
                // 判断文件是否为文件夹
                if (file.isDirectory()) {
                    //是文件夹的话就加上文件夹的最近修改时间
                    textArea.append("文件夹 " + name + " 的最近修改时间为" + lastModified + "\n");
                } else {
                    //是文件的话就加上文件的最近修改时间
                    textArea.append("文件 " + name + " 的最近修改时间为" + lastModified + "\n");
                }
            }
        }
        return null;
    }

    /**
     * 显示帮助信息
     */
    private static void showHelp(String[] args) {
        // 显示帮助信息
        textArea.append("用法: \n");
        textArea.append("命令 -h      查看单条命令的帮助信息\n");
        textArea.append("cd 文件名                  更改当前目录\n");
        textArea.append("md 文件名                  创建新目录\n");
        textArea.append("rd 目录名                  删除一个目录\n");
        textArea.append("ren 旧文件名 新文件名        文件或者是文件夹重命名\n");
        textArea.append("copy 资源 目的路径          复制文件\n");
        textArea.append("move 资源 目的路径          移动文件\n");
        textArea.append("del 文件名                 删除文件\n");
        textArea.append("encrypt 文件名             加密文件\n");
        textArea.append("decrypt 文件名             解密文件\n");
        textArea.append("type 文件名                查看文件的内容\n");
        textArea.append("where 文件名               查找文件\n");
        textArea.append("time 文件名                输出当前目录或文件的最近修改时间\n");
        textArea.append("ls                        列出当前目录下的文件和子目录\n");
        textArea.append("dir                       列出当前目录下的文件和子目录以及他们的最近修改时间和存储大小\n");
        textArea.append("size                      输出当前目录或文件的大小\n");
        textArea.append("输入部分文件名点击tab可以补充文件名         \n");
        textArea.append("help                      展示帮助信息\n");

    }

    /**
     * 检查是否提供了要搜索的文件名
     *
     * @param args
     */
    public static void findFile(String[] args) {
        if (args.length == 0) {
            textArea.append("请提供要搜索的文件。\n");
            return;
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：where 文件名               查找文件\n");
        }
        // 创建一个文件夹对象
        File folder = new File(".");
        // 获取文件夹中的所有文件
        File[] listOfFiles = folder.listFiles();
        // 标记是否找到文件
        boolean found = false;
        // 遍历文件夹中的所有文件
        for (File file : listOfFiles) {
            // 如果是文件并且文件名与要搜索的文件相同
            if (file.getName().equals(args[0])) {
                // 输出文件路径
                textArea.append("找到文件: " + file.getAbsolutePath() + "\n");
                found = true;
            }
        }
        // 如果没有找到文件
        if (!found) {
            textArea.append("没有找到文件。\n");
        }
    }


    public static void getDirectorySizeUtil(String[] args) {
        long size;
        // 如果没有传入参数，则默认使用当前路径
        if (args.length == 0) {
            File file = new File(currentPath);
            size = getDirectorySize(file);
            textArea.append("文件夹" + file.getName() + "的大小为:" + size / 1024 + "MB\n");
            return;
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：size                      输出当前目录或文件的大小\n");
        }
        // 如果传入的参数是一个文件夹，则计算文件夹大小
        File file = new File(args[0]);
        if (file.isDirectory()) {
            size = getDirectorySize(file);
            textArea.append("文件夹" + file.getName() + "的大小为:" + size / 1024 + "MB\n");
        } else {
            // 如果传入的参数是一个文件，则计算文件大小
            size = file.length();
            textArea.append("文件" + file.getName() + "的大小为:" + size / 1024 + "MB\n");
        }
    }


    /**
     * 获取目录大小
     *
     * @param directory 目录
     * @return 目录大小
     */
    public static long getDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归调用获取子目录大小
                size += getDirectorySize(file);
            } else {
                // 获取文件大小
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 其他命令
     */
    private static void unknownCommand(String[] args) {
        textArea.append("无效命令,点击help获取帮助信息。\n");
    }

    /**
     * 文件加密
     */
    private static void encryptFile(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法: encrypt filename" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：encrypt 文件名             加密文件\n");
        } else {
            // 加密文件
            try {
                // 读取文件内容
                String content = new String(Files.readAllBytes(Paths.get(currentPath, args[0])));
                // 加密内容
                String encryptedContent = encrypt(content);
                // 将加密后的内容写入文件
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(currentPath, args[0])));
                writer.write(encryptedContent);
                writer.close();
                textArea.append("文件已加密: " + args[0] + "\n");
            } catch (IOException e) {
                textArea.append("无法加密文件。 " + "\n");
            }
        }
    }

    /**
     * 文件解密
     */
    private static void decryptFile(String[] args) {
        if (args.length == 0) {
            textArea.append("语法错误。用法: decrypt filename" + "\n");
        } else if (args[0].equals("-h")) {
            textArea.append("帮助信息：decrypt 文件名             解密文件\n");
        } else {
            // 解密文件
            try {
                // 读取文件内容
                String content = new String(Files.readAllBytes(Paths.get(currentPath, args[0])));
                // 解密内容
                String decryptedContent = decrypt(content);
                // 将解密后的内容写入文件
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(currentPath, args[0])));
                writer.write(decryptedContent);
                writer.close();
                textArea.append("文件已解密：" + args[0] + "\n");
            } catch (IOException e) {
                textArea.append("无法解密文件。" + "\n");
            }
        }
    }

    /**
     * 加密字符串
     *
     * @param input 待加密字符串
     * @return 加密后的字符串
     */
    private static String encrypt(String input) {
        // 将字符串转换为字节数组
        byte[] inputBytes = input.getBytes();
        // 逐字节进行异或操作
        for (int i = 0; i < inputBytes.length; i++) {
            inputBytes[i] = (byte) (inputBytes[i] ^ 0x5A);
        }
        // 将加密后的字节数组转换为字符串
        return new String(inputBytes);
    }

    /**
     * 解密字符串
     *
     * @param input 待解密字符串
     * @return 解密后的字符串
     */
    private static String decrypt(String input) {
        // 将字符串转换为字节数组
        byte[] inputBytes = input.getBytes();
        // 逐字节进行异或操作
        for (int i = 0; i < inputBytes.length; i++) {
            inputBytes[i] = (byte) (inputBytes[i] ^ 0x5A);
        }
        // 将解密后的字节数组转换为字符串
        return new String(inputBytes);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // 获取文本框中的输入
        String input = textField.getText();
        // 执行输入的命令
        executeCommand(input);
        // 清空文本框
        textField.setText("");
        // 在文本区域中显示当前路径
        textArea.append(currentPath + ">");
    }

    @Override
    // 当键盘输入时触发该方法
    public void keyTyped(KeyEvent e) {
        // 创建一个长度为10的字符串数组
        ArrayList<String> res = new ArrayList<>();
//        String[] res = new String[10];
        // 初始化索引为0
        int index = 0;
        // 如果输入的是Tab键
        if (e.getKeyChar() == VK_TAB) {
            // 获取当前目录
            File currentDir = new File(".");
            // 获取当前目录下的所有文件
            String[] files = currentDir.list();
            // 获取文本框中的文本
            String text = textField.getText();
            // 遍历所有文件
            String prefix = null;
            if (text.contains(" ")) {
                String[] s = text.split(" ");
                prefix = s[0];
                text = s[1];
            }
            System.out.println(text);
            String rule = null;
            for (String file : files) {
                // 创建一个正则表达式，用于匹配以文本框中的文本开头的文件名
                if (text.startsWith(".")) {
                    String replace = text.replace(".", "");
                    rule = "^\\." + replace + ".*";
                } else {
                    rule = "^" + text + ".*";
                }
                // 如果文件名匹配正则表达式
                if (file.matches(rule)) {
                    // 将文件名添加到结果数组中
//                    res[index++] = file;
                    res.add(file);
                }
            }
            // 如果只有一个匹配结果
            if (res.size() == 1) {
                // 将结果设置为文本框中的文本
                if (prefix != null) {
                    textField.setText(prefix + " " + res.get(0));
                } else {
                    textField.setText(res.get(0));
                }

            } else {
                //如果有多个结果，遍历输出结果
                textArea.append("\n");
                for (String re : res) {
                    textArea.append(re + "\t");
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}