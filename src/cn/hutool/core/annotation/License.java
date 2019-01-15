package cn.hutool.core.annotation;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.setting.dialect.Props;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class License {

    private static final String SALT = "HFSZ";
    private String configPath;
    private Props props;

    /**
     * 生成机器码
     * @return
     * @throws LicenseException
     */
    public String generate() throws LicenseException {
        String cpuCode = this.getCPUCode();
        if (StrUtil.isEmpty(cpuCode)){
            throw new LicenseException("获取硬件码失败");
        }

        try {
            String hexCode = HexUtil.encodeHexStr((SALT+cpuCode).toUpperCase());
            Console.log("cpu: {}, hex: {}" , (SALT+cpuCode).toUpperCase(), hexCode);

            String key = StrUtil.format("{}-{}-{}", hexCode.substring(10,14), cpuCode.substring(0,4), hexCode.substring(hexCode.length()-8));
            Console.log("机器码: {}", key);
            return key;
        }catch (Exception e){
            Console.log(e, "激活码计算失败");
            throw new LicenseException("激活码计算失败");
        }
    }

    public boolean check() throws LicenseException {
        String license = props.getStr("license").trim();
        if (StrUtil.isEmpty(license)){
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
        Console.log("文件中激活码: {}", key);
        if(StrUtil.isEmpty(key)){
            throw new LicenseException("获取激活码.");
        }
        String cpuCode = getCPUCode();
        if (StrUtil.isEmpty(cpuCode)){
            throw new LicenseException("获取硬件码失败.");
        }
        String hexCode = HexUtil.encodeHexStr((SALT+cpuCode).toUpperCase());
        String head = hexCode.substring(10,14);
        String end = hexCode.substring(hexCode.length()-8);
        String md5 = SecureUtil.md5( end + head).substring(0,16).toUpperCase();
        String codeMd5 = StrUtil.format("{}-{}-{}", md5.substring(0,4), md5.substring(4, 8), md5.substring(md5.length() - 8));
        return StrUtil.equals(key, codeMd5);
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
            Console.log("生成CPUSerial失败", e);
        }
        return null;
    }

    public License(){
        this.configPath = System.getProperty("user.dir") + File.separator + "config.properties";
        props = new Props(this.configPath);
    }

    public void setLicense(String license){
        props.setProperty("license", license);
        props.store(configPath);
        props.load();
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
        props = new Props(this.configPath);
    }
}
