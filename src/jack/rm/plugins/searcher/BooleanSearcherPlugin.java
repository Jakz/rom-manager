package jack.rm.plugins.searcher;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.pixbits.lib.functional.searcher.SearchParser;
import com.pixbits.lib.functional.searcher.SearchPredicate;
import com.pixbits.lib.parser.shuntingyard.ASTBinary;
import com.pixbits.lib.parser.shuntingyard.ASTNode;
import com.pixbits.lib.parser.shuntingyard.ASTUnary;
import com.pixbits.lib.parser.shuntingyard.ASTValue;
import com.pixbits.lib.parser.shuntingyard.Operator;
import com.pixbits.lib.parser.shuntingyard.ShuntingYardParser;
import com.pixbits.lib.parser.shuntingyard.StackVisitor;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.data.rom.Rom;

public class BooleanSearcherPlugin extends SearchPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Boolean Search Engine", new PluginVersion(1,0), "Jack",
        "This plugins provides boolean expression search parsing.");
  }
    
  private class SimpleSearcher extends SearchParser<Rom>
  {
    final private ShuntingYardParser parser;
    final private EvaluateVisitor visitor;
    
    SimpleSearcher()
    {
      parser = new ShuntingYardParser(
        new Operator("&&", 1, false, false),
        new Operator("||", 2, false, false),
        new Operator("!", 0, true, false)        
      );
      
      visitor = new EvaluateVisitor();
    }
    
    @Override
    public Function<List<SearchPredicate<Rom>>, Predicate<Rom>> parse(String text)
    {
      Function<List<SearchPredicate<Rom>>, Predicate<Rom>> lambda = predicates -> {
              
        ASTNode node = null;
        
        try {
          node = parser.parse(text);
        }
        catch (Exception e)
        {
          // do nothing
        }
        
        if (node == null)
          return r -> true;
        else
        {
          visitor.setPredicates(predicates);
          return node.accept(visitor);
        }     
      };
      
      return lambda;
    }
    
    private class EvaluateVisitor extends StackVisitor<Predicate<Rom>>
    {
      private List<SearchPredicate<Rom>> predicates;

      void setPredicates(List<SearchPredicate<Rom>> predicates)
      {
        reset();
        this.predicates = predicates;
      }

      @Override
      public Predicate<Rom> visitNode(ASTNode node)
      {
        if (node instanceof ASTValue)
        {
          Predicate<Rom> pred = buildSinglePredicate(predicates, ((ASTValue)node).value);
          
          return pred != null ? pred : r -> r.getTitle().toLowerCase().contains(((ASTValue)node).value);
        }
        else if (node instanceof ASTUnary)
          return pop().negate();
        else if (node instanceof ASTBinary)
        {
          Predicate<Rom> o2 = pop(), o1 = pop();
          Operator op = ((ASTBinary)node).operator;
          
          if (op.getMnemonic().equals("&&"))
            return o2.and(o1);
          else
            return o2.or(o1);
        }
        else
          return r -> true;         
      }
    }
  }
  
  final private SimpleSearcher searcher = new SimpleSearcher();
  
  @Override
  public SearchParser<Rom> getSearcher()
  {
    return searcher;
  }

}
