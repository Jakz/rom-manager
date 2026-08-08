package jack.rm.plugins.fetchers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.github.jakz.romlib.data.assets.AssetKind;

public class AssetFetchSelectionDialog
{
  public enum MatchMode
  {
    STRICT,
    BALANCED,
    BROAD
  }

  public static class Entry
  {
    public final AssetKind kind;
    public final URL url;
    boolean available;

    public Entry(AssetKind kind, URL url)
    {
      this.kind = kind;
      this.url = url;
    }

    @Override public String toString()
    {
      return kind.getCaption();
    }
  }

  public static class Result
  {
    public final String name;
    public final int score;
    public final List<Entry> entries;

    public Result(String name, int score, List<Entry> entries)
    {
      this.name = name;
      this.score = score;
      this.entries = entries;
    }

    @Override public String toString()
    {
      return score == 0 ? name : name + " [" + score + "]";
    }
  }

  public static class Selection
  {
    public final Result result;
    public final List<Entry> entries;
    public final MatchMode matchMode;

    Selection(Result result, List<Entry> entries, MatchMode matchMode)
    {
      this.result = result;
      this.entries = entries;
      this.matchMode = matchMode;
    }
  }

  public static Selection choose(Component parent, String title, List<Result> results, Predicate<Entry> availability)
  {
    return choose(parent, title, MatchMode.BALANCED, mode -> results, availability);
  }

  public static Selection choose(Component parent, String title, MatchMode initialMode,
      Function<MatchMode, List<Result>> resultProvider, Predicate<Entry> availability)
  {
    if (initialMode == null)
      initialMode = MatchMode.BALANCED;

    List<Result> results = resultProvider.apply(initialMode);
    if (results.isEmpty())
      return null;

    DefaultListModel<Result> model = new DefaultListModel<>();
    results.forEach(model::addElement);

    JList<Result> list = new JList<>(model);
    list.setSelectedIndex(0);

    JComboBox<MatchMode> modeBox = new JComboBox<>(MatchMode.values());
    modeBox.setSelectedItem(initialMode);

    JPanel entriesPanel = new JPanel(new GridLayout(0, 1));
    JLabel preview = new JLabel("Select a result", SwingConstants.CENTER);
    preview.setPreferredSize(new Dimension(320, 320));

    SelectionState state = new SelectionState();
    Map<URL, ImageIcon> previewCache = new HashMap<>();

    Runnable refreshEntries = () -> loadEntries(list.getSelectedValue(), availability, entriesPanel, preview, state,
        previewCache);
    list.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting())
        refreshEntries.run();
    });
    modeBox.addActionListener(e -> {
      MatchMode mode = (MatchMode)modeBox.getSelectedItem();
      List<Result> nextResults = resultProvider.apply(mode);

      model.clear();
      nextResults.forEach(model::addElement);

      if (!nextResults.isEmpty())
        list.setSelectedIndex(0);
      else
      {
        entriesPanel.removeAll();
        state.checkboxes.clear();
        preview.setIcon(null);
        preview.setText("No results");
      }
    });

    JPanel right = new JPanel(new BorderLayout());
    right.add(entriesPanel, BorderLayout.NORTH);
    right.add(preview, BorderLayout.CENTER);

    JPanel left = new JPanel(new BorderLayout());
    left.add(modeBox, BorderLayout.NORTH);
    left.add(new JScrollPane(list), BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    split.setPreferredSize(new Dimension(820, 380));
    split.setDividerLocation(360);

    refreshEntries.run();

    int result = JOptionPane.showConfirmDialog(parent, split, title, JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE);

    if (result != JOptionPane.OK_OPTION)
      return null;

    Result selected = list.getSelectedValue();
    List<Entry> selectedEntries = state.checkboxes.stream()
        .filter(JCheckBox::isSelected)
        .map(box -> (Entry)box.getClientProperty("entry"))
        .collect(Collectors.toList());

    return selectedEntries.isEmpty() ? null : new Selection(selected, selectedEntries,
        (MatchMode)modeBox.getSelectedItem());
  }

  private static void loadEntries(Result result, Predicate<Entry> availability, JPanel entriesPanel, JLabel preview,
      SelectionState state, Map<URL, ImageIcon> previewCache)
  {
    entriesPanel.removeAll();
    state.checkboxes.clear();
    preview.setText("Checking...");
    preview.setIcon(null);

    if (result == null)
      return;

    new SwingWorker<List<Entry>, Void>() {
      @Override protected List<Entry> doInBackground() {
        List<Entry> available = new ArrayList<>();

        for (Entry entry : result.entries)
        {
          entry.available = availability.test(entry);
          if (entry.available)
            available.add(entry);
        }

        return available;
      }

      @Override protected void done() {
        try
        {
          List<Entry> available = get();
          entriesPanel.removeAll();
          state.checkboxes.clear();

          if (available.isEmpty())
          {
            entriesPanel.add(new JLabel("No assets available"));
            preview.setText("No preview");
          }
          else
          {
            for (Entry entry : available)
            {
              JCheckBox box = new JCheckBox(entry.kind.getCaption(), true);
              box.putClientProperty("entry", entry);
              box.addActionListener(e -> loadPreview(entry.url, preview, previewCache));
              box.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                  loadPreview(entry.url, preview, previewCache);
                }
              });
              state.checkboxes.add(box);
              entriesPanel.add(box);
            }

            loadPreview(available.get(0).url, preview, previewCache);
          }

          entriesPanel.revalidate();
          entriesPanel.repaint();
        }
        catch (Exception e)
        {
          preview.setText("Unable to load assets");
        }
      }
    }.execute();
  }

  private static void loadPreview(URL url, JLabel preview, Map<URL, ImageIcon> previewCache)
  {
    ImageIcon cached = previewCache.get(url);
    if (cached != null)
    {
      preview.setText("");
      preview.setIcon(cached);
      return;
    }

    preview.setText("Loading...");
    preview.setIcon(null);

    new SwingWorker<ImageIcon, Void>() {
      @Override protected ImageIcon doInBackground() throws Exception {
        Image image = ImageIO.read(url);
        if (image == null)
          return null;

        return containedIcon(image, 320);
      }

      @Override protected void done() {
        try
        {
          ImageIcon icon = get();
          if (icon != null)
          {
            previewCache.put(url, icon);
            preview.setText("");
            preview.setIcon(icon);
          }
          else
            preview.setText("No preview");
        }
        catch (Exception e)
        {
          preview.setText("Not found");
        }
      }
    }.execute();
  }

  private static ImageIcon containedIcon(Image image, int size)
  {
    int width = image.getWidth(null);
    int height = image.getHeight(null);

    if (width <= 0 || height <= 0)
      return null;

    double scale = Math.min(size / (double)width, size / (double)height);
    int drawWidth = Math.max(1, (int)(width * scale));
    int drawHeight = Math.max(1, (int)(height * scale));
    int x = (size - drawWidth) / 2;
    int y = (size - drawHeight) / 2;

    java.awt.image.BufferedImage target = new java.awt.image.BufferedImage(size, size,
        java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = target.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(image, x, y, drawWidth, drawHeight, null);
    g.dispose();

    return new ImageIcon(target);
  }

  private static class SelectionState
  {
    final List<JCheckBox> checkboxes = new ArrayList<>();
  }
}
