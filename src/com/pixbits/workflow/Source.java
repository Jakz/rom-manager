package com.pixbits.workflow;

import java.util.function.Supplier;

public interface Source<T extends Data> extends Supplier<T>
{
  
}