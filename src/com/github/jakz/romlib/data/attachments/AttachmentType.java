package com.github.jakz.romlib.data.attachments;

public enum AttachmentType
{
  IPS_PATCH("IPS Patch", IPS.class),
  OTHER("Other", null)
  ;
  
  private final String caption;
  private final Class<? extends Enum<? extends Subtype>> subType;
  
  AttachmentType(String caption) { this(caption, null); } 
  AttachmentType(String caption, Class<? extends Enum<? extends Subtype>> subType)
  {
    this.caption = caption;
    this.subType = subType;
  }
  
  public String getCaption() { return caption; }
  public boolean hasSubType() { return subType != null; }
  public Subtype[] getSubTypes()
  {
    if (subType == null)
      return null;
    else
    {
      try
      {
        return (Subtype[])subType.getMethod("values").invoke(null);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return null;
      }
    }
  }
  
  
  public static interface Subtype
  {
    public String getCaption();
  }
  
  public static enum IPS implements Subtype
  {
    FIX("Fix"),
    TRAINER("Trainer"),
    CHEAT("Cheat"),
    MISC("Misc")
    ;
    
    private final String caption;
    IPS(String caption) { this.caption = caption; }
    public String getCaption() { return caption; }
  }
}
