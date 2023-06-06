package com.cislc.shadow.manage.device.init;

import com.cislc.shadow.manage.common.utils.ClassUtils;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.shadow.EntityFactory;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import com.cislc.shadow.manage.device.entity.SduCabinet;
import com.cislc.shadow.manage.device.repository.SduCabinetRepository;
import com.cislc.shadow.manage.device.repository.SduGateRepository;
import com.cislc.shadow.manage.device.repository.SduMediaRepository;
import com.cislc.shadow.utils.mqtts.MqttFactory;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@Slf4j
public class ShadowInit implements CommandLineRunner {
  @Value("${shadow.auto-init:false}")
  private Boolean autoInit;

  @Autowired
  private SduGateRepository sduGateRepository;

  @Autowired
  private SduMediaRepository sduMediaRepository;

  @Autowired
  private SduCabinetRepository sduCabinetRepository;

  @Override
  public void run(String[] args) throws Exception {
    if (autoInit) {
      Map<String, ShadowEntity> dataMap = new HashMap<>();
      List<SduCabinet> sduCabinetList = sduCabinetRepository.findAll();
      EntityFactory.destroyEntities();
      List<String> entityNames = ClassUtils.getAllEntityName();
      for (SduCabinet sduCabinet : sduCabinetList) {
        dataMap.put(sduCabinet.getTopic(), sduCabinet);
        EntityFactory.injectEntities(sduCabinet, sduCabinet.getTopic(), entityNames);
      }
      boolean injectResult = ShadowFactory.batchInjectShadow(dataMap);
      MqttFactory.subscribe("update/bindDevice", 0);
    }
  }
}
