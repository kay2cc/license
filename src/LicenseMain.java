public class LicenseMain {
/*

    public static void main(String[] args) {
        // 3137-CSZH-30463131
        LicenseMain license = new LicenseMain();
        console("\n *** PT License Start ***\n");
        console("请输出机器码!  (回车计算激活码)");
        Scanner scanner = new Scanner(System.in);

        String hostCode = scanner.next();

        // 生成激活码
        String generateCode = null;
        try {
            generateCode = license.generate(hostCode);
            console("\n生成激活码：\n" + generateCode);
        } catch (Exception e) {
            console("激活码生成失败，请检查机器码是否正确。");
            e.printStackTrace();
        }
    }

    */
/**
     * 生成激活码
     * @param key
     * @return
     * @throws Exception
     *//*

    public String generate(String key) throws Exception {
        key = key.replaceAll("-", "");
        String head = key.substring(0, 4);
        String end = key.substring(key.length() - 8);
        String md5 = SecureUtil.md5(end + head).substring(0, 16).toUpperCase();
        return StrUtil.format("{}-{}-{}", md5.substring(0, 4), md5.substring(4, 8), md5.substring(md5.length() - 8));
    }
*/


    private static void console(String log) {
        System.out.println(log);
    }

    /*public static void main(String[] args) throws LicenseException {

        License license = new License();

        if (license.check()){
            BeginPanel bgpane = new BeginPanel();
            bgpane.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }else {
            // 进行软件授权
            licensePane(license);
        }
    }

    private static void licensePane(License license) throws LicenseException {
        String licenseMsg = StrUtil.format("软件未授权激活。\n" +
                "机器码：\n{} \n" +
                "\n请将机器码发送给xxx，申请软件授权码。\n\n" +
                "如有激活码请点击   “进行激活”。\n\n\n", license.generate());
        Object[] options ={ "进行激活", "关闭软件" };

        int result = JOptionPane.showOptionDialog(null,
                licenseMsg,
                "软件无许可",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (result == 0){
            String licStr = JOptionPane.showInputDialog(null,
                    "请输入激活码：\n\n\n",
                    "软件授权",
                    JOptionPane.INFORMATION_MESSAGE);

            if (StrUtil.isBlank(licStr)){
                System.exit(0);
            }

            Props props = new Props(System.getProperty("user.dir")+"\\config.properties");
            props.setProperty("license", licStr);
            props.store(System.getProperty("user.dir")+"\\config.properties");
            String msg = "授权失败！\n" +
                    "请确定机器码与激活码一致。\n" +
                    "机器码：\n" + license.generate();
            if (license.check()){
                msg = "授权成功！请重新打开此软件";
            }

            JOptionPane.showMessageDialog(null, msg);
            System.exit(0);
        }
    }*/

}
