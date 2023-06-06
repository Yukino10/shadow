package com.cislc.shadow.manage.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.bean.field.DatabaseField;
import com.cislc.shadow.manage.core.bean.field.EntityField;
import java.lang.Boolean;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(
    name = "sdu_media"
)
public class SduMedia extends ShadowEntity {
  @Transient
  private static final Map<String, DatabaseField> databaseFieldMap = new HashMap<>();

  static {
    databaseFieldMap.put("path", new DatabaseField("sdu_media", "path"));
    databaseFieldMap.put("mime", new DatabaseField("sdu_media", "mime"));
    databaseFieldMap.put("name", new DatabaseField("sdu_media", "name"));
    databaseFieldMap.put("anPath", new DatabaseField("sdu_media", "an_path"));
    databaseFieldMap.put("downloaded", new DatabaseField("sdu_media", "downloaded"));
  }

  @Column(
      name = "name"
  )
  private String name;

  @Column(
      name = "mime"
  )
  private String mime;

  @Column(
      name = "path"
  )
  private String path;

  @Column(
      name = "downloaded"
  )
  private Boolean downloaded;

  @Column(
      name = "an_path"
  )
  private String anPath;

  public SduMedia() {
    super();
  }

  public SduMedia(String deviceId) {
    super(deviceId);
    setDeviceId(deviceId);
  }

  @JSONField(
      serialize = false
  )
  @Transient
  @Override
  public boolean isDevice() {
    return false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    EntityField field = new EntityField("SduMedia", "name", this.name);
    this.name = name;
    field.setFieldValue(name);
    notifyObservers(databaseFieldMap.get("name"), field);
  }

  public String getMime() {
    return mime;
  }

  public void setMime(String mime) {
    EntityField field = new EntityField("SduMedia", "mime", this.mime);
    this.mime = mime;
    field.setFieldValue(mime);
    notifyObservers(databaseFieldMap.get("mime"), field);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    EntityField field = new EntityField("SduMedia", "path", this.path);
    this.path = path;
    field.setFieldValue(path);
    notifyObservers(databaseFieldMap.get("path"), field);
  }

  public Boolean getDownloaded() {
    return downloaded;
  }

  public void setDownloaded(Boolean downloaded) {
    EntityField field = new EntityField("SduMedia", "downloaded", this.downloaded);
    this.downloaded = downloaded;
    field.setFieldValue(downloaded);
    notifyObservers(databaseFieldMap.get("downloaded"), field);
  }

  public String getAnPath() {
    return anPath;
  }

  public void setAnPath(String anPath) {
    EntityField field = new EntityField("SduMedia", "anPath", this.anPath);
    this.anPath = anPath;
    field.setFieldValue(anPath);
    notifyObservers(databaseFieldMap.get("anPath"), field);
  }
}
