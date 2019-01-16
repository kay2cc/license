package cn.hutool.core.annotation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.Scanner;

public class License {

    private static final String SALT = "HFSZ";
    private String configPath;

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 生成机器码
     * @return
     * @throws LicenseException
     */
    public String generate() throws LicenseException {
        String cpuCode = this.getCPUCode();
        if (isEmpty(cpuCode)){
            throw new LicenseException("获取硬件码失败");
        }

        try {
            String hexCode = new String(encodeHex(bytes((SALT+cpuCode).toUpperCase())));

            String key = String.format("%s-%s-%s", hexCode.substring(10,14), cpuCode.substring(0,4), hexCode.substring(hexCode.length()-8));
            log("机器码: " + key);
            return key;
        }catch (Exception e){
            e.printStackTrace();
            log("激活码计算失败");
            throw new LicenseException("激活码计算失败");
        }
    }

    public boolean check() throws LicenseException {
        String license = getLicense();
        if (isEmpty(license)){
            return false;
        }

        return activation(license);
    }

    /**
     * 软件激活 校验
     * @param key
     * @return
     * @throws LicenseException
     */
    public boolean activation(String key) throws LicenseException {
        log("文件中激活码: " + key);
        if(isEmpty(key)){
            throw new LicenseException("获取激活码.");
        }
        String cpuCode = getCPUCode();
        if (isEmpty(cpuCode)){
            throw new LicenseException("获取硬件码失败.");
        }
        String hexCode = new String(encodeHex(bytes((SALT+cpuCode).toUpperCase())));
        String head = hexCode.substring(10,14);
        String end = hexCode.substring(hexCode.length()-8);
        String md5 = encrypt( end + head).substring(0,16).toUpperCase();
        String codeMd5 = String.format("%s-%s-%s", md5.substring(0,4), md5.substring(4, 8), md5.substring(md5.length() - 8));
        return key.equals(codeMd5);
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

    public License(){
        this.configPath = System.getProperty("user.dir") + File.separator + "config.properties";
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    private String encrypt(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes(StandardCharsets.UTF_8));
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

    public boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    private void log(String log){
        System.out.println(log);
    }

    public String getLicense(){
        return this.getValueByKey("license");
    }

    public void setLicense(String license) throws IOException {
        writeProperties("license", license);
    }

    private String getValueByKey(String key) {
        Properties pps = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(configPath));
            pps.load(in);
            return  pps.getProperty(key);
        }catch (IOException e) {
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

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters from the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    private static byte[] bytes(CharSequence str) {
        if (str == null) {
            return null;
        }

        if (null == StandardCharsets.UTF_8) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(StandardCharsets.UTF_8);
    }

}
