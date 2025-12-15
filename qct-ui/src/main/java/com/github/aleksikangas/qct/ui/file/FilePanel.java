package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class FilePanel extends AbstractPanel {
  private final JTextField fileTextField = new JTextField();
  private final JButton fileBrowseButton = new JButton("Browse...");
  private final JButton decodeButton = new JButton("Decode");

  private final transient Controller controller;

  public FilePanel(final Controller controller) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[grow][grow]", "[fill, grow][fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    setBorder(BorderFactory.createTitledBorder("File"));

    fileTextField.setEnabled(false);
    fileBrowseButton.addActionListener(e -> selectFile());
    decodeButton.addActionListener(e -> decodeSelectedFile());

    setDecodeButtonEnabled(false);

    add(fileTextField, "grow, span 2, wrap");
    add(fileBrowseButton, "");
    add(decodeButton, "alignx right, wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    fileBrowseButton.setEnabled(true);
    setDecodeButtonEnabled(true);
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    fileBrowseButton.setEnabled(true);
    setDecodeButtonEnabled(true);
  }

  private void selectFile() {
    final var fileChooser = new JFileChooser();
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("QCT file", "qct"));
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      final File selectedFile = fileChooser.getSelectedFile();
      fileTextField.setText(selectedFile.getPath());
    }
    setDecodeButtonEnabled(true);
  }

  private void decodeSelectedFile() {
    fileBrowseButton.setEnabled(false);
    setDecodeButtonEnabled(false);
    controller.decodeQctFile(Path.of(fileTextField.getText()));
  }

  private void setDecodeButtonEnabled(final boolean enabled) {
    decodeButton.setEnabled(!fileTextField.getText().isBlank() && Files.exists(Path.of(fileTextField.getText())) && enabled);
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<FilePanel> {
    private final Event<DecodeRequestEvent> decodeRequestEventPublisher;

    @Inject
    public Controller(final Event<DecodeRequestEvent> decodeRequestEventPublisher) {
      this.decodeRequestEventPublisher = Objects.requireNonNull(decodeRequestEventPublisher);
    }

    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new FilePanel(this));
    }

    private void decodeQctFile(final Path qctFilePath) {
      decodeRequestEventPublisher.fireAsync(new DecodeRequestEvent(qctFilePath));
    }
  }
}
