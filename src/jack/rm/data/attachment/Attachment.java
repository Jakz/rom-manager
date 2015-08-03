package jack.rm.data.attachment;

import java.nio.file.Path;

public class Attachment
{
  private Path filename;
  private String description;
  private AttachmentType type;
  private AttachmentType.Subtype subtype;
  
  Attachment()
  {
    
  }
  
  public String getDescription() { return description; }
  public AttachmentType getType() { return type; }
  public AttachmentType.Subtype getSubType() { return subtype; }
  
  public void setDescription(String v) { this.description = v; }
  public void setType(AttachmentType t) { this.type = t; }
  public void setSubType(AttachmentType.Subtype t) { this.subtype = t; }
}
