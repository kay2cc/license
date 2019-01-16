import cn.hutool.core.annotation.License;
import cn.hutool.core.annotation.LicenseException;

import java.io.IOException;

/**
 * @author kaiccc
 * @date 2019-01-16 13:46
 */
public class Main {
    public static void main(String[] args) throws LicenseException, IOException {

        License license = new License();

        System.out.println(license.generate());
        license.setLicense("A3D2-A3C3-2902E53D");
        System.out.println(license.check());

        String licenseMsg = String.format("软件未授权激活。\n" +
                "机器码：\n%s \n" +
                "\n请将机器码发送给xxx，申请软件授权码。\n\n" +
                "如有激活码请点击   “进行激活”。\n\n\n", license.generate());

        System.out.println(licenseMsg);
    }
}
