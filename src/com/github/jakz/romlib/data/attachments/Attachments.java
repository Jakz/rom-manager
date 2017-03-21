package com.github.jakz.romlib.data.attachments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Attachments implements Iterable<Attachment>
{
  private final List<Attachment> attachments;
  
  public Attachments()
  {
    attachments = new ArrayList<Attachment>();
  }
  
  public int size() { return attachments.size(); }
  public Attachment get(int index) { return attachments.get(index); }
  public void add(Attachment attachment) { attachments.add(attachment); }
  public Iterator<Attachment> iterator() { return attachments.iterator(); }
  public Stream<Attachment> stream() { return attachments.stream(); }
  public List<Attachment> data() { return attachments; }
  
  public void set(List<Attachment> attachments)
  {
    this.attachments.clear();
    this.attachments.addAll(attachments);
  }
}
