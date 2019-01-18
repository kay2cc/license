package cn.hutool.core.annotation;

import java.io.*;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class License {

    private String configPath;

    /**
     * 生成机器码
     *
     * @return
     * @throws LicenseException
     */
    public String generate(String salt) throws LicenseException {
        String cpuCode = this.getCPUCode();
        if (isEmpty(cpuCode)) {
            throw new LicenseException("获取硬件码失败");
        }

        try {
            String data = encrypt(salt + cpuCode).toUpperCase();

            String key = String.format("%s-%s-%s", cpuCode.substring(0, 4), salt, data.substring(data.length() - 8));
            log("机器码: " + key);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            throw new LicenseException("激活码计算失败");
        }
    }

    public String generate() throws LicenseException {
        return generate(now());
    }

    public boolean check() throws LicenseException, ParseException {
        String license = getLicense();
        if (isEmpty(license)) {
            return false;
        }
        String[] keys = license.split(",");
        if (keys.length != 2) {
            throw new LicenseException("配置文件内的激活码错误");
        }

        return activation(keys[0], keys[1]);
    }

    /**
     * 软件激活 校验
     */
    private boolean activation(String hostCode, String license) throws LicenseException, ParseException {
        log(String.format("文件中机器码: %s, 激活码: %s" ,hostCode, license));

        String cpuCode = getCPUCode();
        if (isEmpty(cpuCode)) {
            throw new LicenseException("获取硬件码失败.");
        }

        // 生成当时机器码
        String checkHostCode = generate(hostCode.substring(5, 9));

        if (!checkHostCode.equals(hostCode)){
            throw new LicenseException("激活失败,机器码不一致");
        }
        // 超时检测
        dateCheck(hostCode.substring(5, 9));
        // 生成激活码
        String codeMd5 = generateMd5(hostCode);
        // 激活码比对
        if (!license.equals(codeMd5)) {
            throw new LicenseException("激活失败,错误的激活码");
        }

        return true;
    }

    /**
     * 激活码
     * @param key
     * @return
     */
    public String generateMd5(String key) {
        key = key.replaceAll("-", "");
        // 盐
        String head = key.substring(4, 8);
        String end = key.substring(key.length() - 8);
        String md5 = encrypt(end + head).substring(0, 16).toUpperCase();
        return String.format("%s-%s-%s", md5.substring(0, 4), md5.substring(4, 8), md5.substring(md5.length() - 8));
    }

    /**
     * CPU序列号
     *
     * @return
     */
    private String getCPUCode() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            sc.next();
            return sc.next();
        } catch (IOException e) {
            e.printStackTrace();
            log("生成CPUSerial失败");
        }
        return null;
    }

    public License() {
        this.configPath = System.getProperty("user.dir") + File.separator + "config.properties";
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String encrypt(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes());
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();
            for (byte b : s) {
                result.append(Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    private void log(String log) {
        System.out.println(log);
    }

    public String getLicense() {
        return this.getValueByKey("license");
    }

    public void setLicense(String license) throws IOException, LicenseException {
        writeProperties("license", generate() + "," + license);
    }

    private String getValueByKey(String key) {
        Properties pps = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(configPath));
            pps.load(in);
            return pps.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeProperties(String pKey, String pValue) throws IOException {
        Properties pps = new Properties();
        InputStream in = new FileInputStream(configPath);
        pps.load(in);
        OutputStream out = new FileOutputStream(configPath);
        pps.setProperty(pKey, pValue);
        pps.store(out, "Update " + pKey + " name");
    }

    private String now() {
        SimpleDateFormat sft = new SimpleDateFormat("yyMM");
        return sft.format(new Date());
    }

    private void dateCheck(String dateCode) throws ParseException, LicenseException {
        SimpleDateFormat sft = new SimpleDateFormat("yyMM");
        Date date = sft.parse(dateCode);
        int days = (int) ((System.currentTimeMillis() - date.getTime()) / (1000 * 60 * 60 * 24));

        if (days > 365 + 31){
            throw new LicenseException("软件授权时间超过一年，请重新申请授权码");
        }
    }

}
