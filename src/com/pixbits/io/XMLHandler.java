package com.pixbits.io;

import org.xml.sax.helpers.DefaultHandler;

public abstract class XMLHandler<T> extends DefaultHandler
{
  abstract T get();
}
