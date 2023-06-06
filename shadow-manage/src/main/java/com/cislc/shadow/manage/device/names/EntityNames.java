package com.cislc.shadow.manage.device.names;

import com.cislc.shadow.utils.enums.Encryption;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

public interface EntityNames {
  List<String> entityNames = Arrays.asList("SduGate","SduMedia","SduCabinet","VideoAttr");

  String deviceName = "SduCabinet";

  Encryption encryption = Encryption.NONE;
}
