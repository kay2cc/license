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
    }
}
