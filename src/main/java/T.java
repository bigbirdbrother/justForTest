import java.io.*;
import java.text.DecimalFormat;

/**
 * @author dufz
 * @version 1.0
 * <p>
 * Copyright 2020 Beijing Uxsino Software Co., Ltd./Branch Of Xi'an
 * All right reserved.
 * @date 2020/12/4 11:03
 */
public class T {
    private static Integer code = 0;
    private static Integer codeComments = 0;
    private static Integer codeBlank = 0;
    private static DecimalFormat df  = new DecimalFormat("###.00");//这样为保持2位
    public static void main(String[] args) {
        File file = new File("C:\\Users\\uxdb\\OneDrive\\桌面\\Diffcount\\diffcount\\test\\new\\webAgent\\src");
        factFiles(file);
        System.out.println("纯代码行数" + code);
        System.out.println("空白行数" + codeBlank);
        System.out.println("注释行数" + codeComments);
        System.out.println("总行数" + (codeComments + codeBlank + code));
        //double codebase2=(double)(Math.round(code/(codeComments + codeBlank + code)/100.0));//这样为保持2位
        double coderate=(double)code*100/(codeComments + codeBlank + code);
        //System.out.println("纯代码行率"+codebase2);
        System.out.println("纯代码行率%"+df.format(coderate));
        System.out.println("空行率%"+df.format((double)codeBlank*100.00/(codeComments + codeBlank + code)));
        System.out.println("注释行率%"+df.format((double)codeComments*100.00/(codeComments + codeBlank + code)));
    }

    public static void factFiles(File file) {
        BufferedReader br = null;
        String s = null;

        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f : files) {
                factFiles(f);
            }
        } else {
            if (file.getName().contains(".java")) {
                try {
                    br = new BufferedReader(new FileReader(file));
                    boolean comm = false;
                    while((s = br.readLine()) != null) {
                        s = s.trim();
                        if(s.startsWith("/*") && s.endsWith("*/")) {
                            codeComments++;
                        } else if(s.trim().startsWith("//")) {
                            codeComments++;
                        } else if(s.startsWith("/*") && !s.endsWith("*/")) {
                            codeComments++;
                            comm = true;
                        } else if(!s.startsWith("/*") && s.endsWith("*/")) {
                            codeComments++;
                            comm = false;
                        } else if(comm) {
                            codeComments++;
                        } else if(s.trim().length() < 1) {
                            codeBlank++;
                        } else {
                            code++;
                        }
                    }
                    br.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
