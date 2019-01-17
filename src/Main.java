import cn.hutool.core.annotation.License;

/**
 * @author kaiccc
 * @date 2019-01-16 13:46
 */
public class Main {
    public static void main(String[] args) throws Exception {

        License license = new License();

//        license.dateCheck("1801");


//        String lic = license.generate();
//
//        license.setLicense(license.generate(lic));
        System.out.println("check: " +license.check());

    }

    public String generate(String key, License license ) {
        key = key.replaceAll("-", "");
        // 时间戳
        String head = key.substring(4, 8);

        String end = key.substring(key.length() - 8);
        String md5 = license.encrypt(end + head).substring(0, 16).toUpperCase();
        return String.format("%s-%s-%s", md5.substring(0, 4), md5.substring(4, 8), md5.substring(md5.length() - 8));
    }
}
