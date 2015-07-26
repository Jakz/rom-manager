package com.pixbits.workflow;

import java.util.function.Supplier;

public interface Source<T extends WorkflowData> extends Supplier<T>
{
  
}