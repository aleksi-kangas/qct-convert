package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.Objects;

public final class MetadataPanel extends AbstractPanel {
  private final transient Controller controller;

  private final JTextField magicNumberField = new JTextField();
  private final JTextField fileFormatVersionField = new JTextField();
  private final JTextField widthField = new JTextField();
  private final JTextField heightField = new JTextField();
  private final JTextField longTitleField = new JTextField();
  private final JTextField nameField = new JTextField();
  private final JTextField identifierField = new JTextField();
  private final JTextField editionField = new JTextField();
  private final JTextField revisionField = new JTextField();
  private final JTextField keywordsField = new JTextField();
  private final JTextField copyrightField = new JTextField();
  private final JTextField scaleField = new JTextField();
  private final JTextField datumField = new JTextField();
  private final JTextField depthsField = new JTextField();
  private final JTextField heightsField = new JTextField();
  private final JTextField projectionField = new JTextField();
  private final JTextField flagsField = new JTextField();
  private final JTextField originalFileNameField = new JTextField();
  private final JTextField originalFileSizeField = new JTextField();
  private final JTextField originalFileCreationTimeField = new JTextField();

  // --- Extended Data ---
  private final JTextField mapTypeField = new JTextField();
  private final JTextField diskNameField = new JTextField();
  private final JTextField associatedDataField = new JTextField();
  // --- Extended Data ---

  public MetadataPanel(final Controller controller, final DatumShiftPanel datumShiftPanel, final LicenseInformationPanel licenseInformationPanel, final DigitalMapShopPanel digitalMapShopPanel, final MapOutlinePanel mapOutlinePanel) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[][fill, grow]", ""));
    this.controller = Objects.requireNonNull(controller);
    setBorder(BorderFactory.createTitledBorder("Metadata"));

    magicNumberField.setEnabled(false);
    fileFormatVersionField.setEnabled(false);
    widthField.setEnabled(false);
    heightField.setEnabled(false);
    longTitleField.setEnabled(false);
    nameField.setEnabled(false);
    identifierField.setEnabled(false);
    editionField.setEnabled(false);
    revisionField.setEnabled(false);
    keywordsField.setEnabled(false);
    copyrightField.setEnabled(false);
    scaleField.setEnabled(false);
    datumField.setEnabled(false);
    depthsField.setEnabled(false);
    heightsField.setEnabled(false);
    projectionField.setEnabled(false);
    flagsField.setEnabled(false);
    originalFileNameField.setEnabled(false);
    originalFileSizeField.setEnabled(false);
    originalFileCreationTimeField.setEnabled(false);
    mapTypeField.setEnabled(false);
    diskNameField.setEnabled(false);
    associatedDataField.setEnabled(false);

    add(new JLabel("Magic Number:"));
    add(magicNumberField, "wrap");

    add(new JLabel("File Format Version:"));
    add(fileFormatVersionField, "wrap");

    add(new JLabel("Width (tiles / pixels):"));
    add(widthField, "wrap");

    add(new JLabel("Height (tiles / pixels):"));
    add(heightField, "wrap");

    add(new JLabel("Long Title:"));
    add(longTitleField, "wrap");

    add(new JLabel("Name:"));
    add(nameField, "wrap");

    add(new JLabel("Identifier:"));
    add(identifierField, "wrap");

    add(new JLabel("Edition:"));
    add(editionField, "wrap");

    add(new JLabel("Revision:"));
    add(revisionField, "wrap");

    add(new JLabel("Keywords:"));
    add(keywordsField, "wrap");

    add(new JLabel("Copyright:"));
    add(copyrightField, "wrap");

    add(new JLabel("Scale:"));
    add(scaleField, "wrap");

    add(new JLabel("Datum:"));
    add(datumField, "wrap");

    add(new JLabel("Depths:"));
    add(depthsField, "wrap");

    add(new JLabel("Heights:"));
    add(heightsField, "wrap");

    add(new JLabel("Projection:"));
    add(projectionField, "wrap");

    add(new JLabel("Flags:"));
    add(flagsField, "wrap");

    add(new JLabel("Original File Name:"));
    add(originalFileNameField, "wrap");

    add(new JLabel("Original File Size:"));
    add(originalFileSizeField, "wrap");

    add(new JLabel("Original File Creation Time:"));
    add(originalFileCreationTimeField, "wrap");

    add(new JLabel("Map Type:"));
    add(mapTypeField, "wrap");

    add(new JLabel("Datum Shift:"));
    add(datumShiftPanel, "wrap");

    add(new JLabel("Disk Name:"));
    add(diskNameField, "wrap");

    add(new JLabel("License Information"));
    add(licenseInformationPanel, "wrap");

    add(new JLabel("Associated Data:"));
    add(associatedDataField, "wrap");

    add(new JLabel("Digital Map Shop:"));
    add(digitalMapShopPanel, "wrap");

    add(new JLabel("Map Outline:"));
    add(mapOutlinePanel, "wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    final Metadata metadata = event.qctFile().metadata();
    magicNumberField.setText(metadata.magicNumber().toString());
    fileFormatVersionField.setText(metadata.fileFormatVersion().toString());
    widthField.setText(String.format("%d / %d", metadata.widthTiles(), metadata.widthPixels()));
    heightField.setText(String.format("%d / %d", metadata.heightTiles(), metadata.heightPixels()));
    longTitleField.setText(metadata.longTitle());
    nameField.setText(metadata.name());
    identifierField.setText(metadata.identifier());
    editionField.setText(metadata.edition());
    revisionField.setText(metadata.revision());
    keywordsField.setText(metadata.keywords());
    copyrightField.setText(metadata.copyright());
    scaleField.setText(metadata.scale());
    datumField.setText(metadata.datum());
    depthsField.setText(metadata.depths());
    heightsField.setText(metadata.heights());
    projectionField.setText(metadata.projection());
    flagsField.setText(metadata.flags().toString());
    originalFileNameField.setText(metadata.originalFileName());
    originalFileSizeField.setText(String.valueOf(metadata.originalFileSize()));
    originalFileSizeField.setToolTipText(String.format("KB: %d%n0MB: %d%nGB: %.2f",
                                                       metadata.originalFileSize() / 1000,
                                                       metadata.originalFileSize() / 1000 / 1000,
                                                       metadata.originalFileSize() / 1000.0 / 1000.0 / 1000.0));
    originalFileCreationTimeField.setText(metadata.originalFileCreationTime().toString());
    mapTypeField.setText(metadata.extendedData().mapType());
    diskNameField.setText(metadata.extendedData().diskName());
    associatedDataField.setText(metadata.extendedData().associatedData());
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    magicNumberField.setText("");
    fileFormatVersionField.setText("");
    widthField.setText("");
    heightsField.setText("");
    longTitleField.setText("");
    nameField.setText("");
    identifierField.setText("");
    editionField.setText("");
    revisionField.setText("");
    keywordsField.setText("");
    copyrightField.setText("");
    scaleField.setText("");
    datumField.setText("");
    depthsField.setText("");
    heightsField.setText("");
    projectionField.setText("");
    flagsField.setText("");
    originalFileNameField.setText("");
    originalFileSizeField.setText("");
    originalFileCreationTimeField.setText("");
    mapTypeField.setText("");
    diskNameField.setText("");
    associatedDataField.setText("");
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<MetadataPanel> {
    private final DatumShiftPanel.Controller datumShiftPanelController;
    private final LicenseInformationPanel.Controller licenseInformationPanelController;
    private final DigitalMapShopPanel.Controller digitalMapShopPanelController;
    private final MapOutlinePanel.Controller mapOutlinePanelController;

    @Inject
    public Controller(final DatumShiftPanel.Controller datumShiftPanelController,
                      final LicenseInformationPanel.Controller licenseInformationPanelController,
                      final DigitalMapShopPanel.Controller digitalMapShopPanelController,
                      final MapOutlinePanel.Controller mapOutlinePanelController) {
      this.datumShiftPanelController = Objects.requireNonNull(datumShiftPanelController);
      this.licenseInformationPanelController = Objects.requireNonNull(licenseInformationPanelController);
      this.digitalMapShopPanelController = Objects.requireNonNull(digitalMapShopPanelController);
      this.mapOutlinePanelController = Objects.requireNonNull(mapOutlinePanelController);
    }

    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new MetadataPanel(this,
                                                          datumShiftPanelController.getPanel(),
                                                          licenseInformationPanelController.getPanel(),
                                                          digitalMapShopPanelController.getPanel(),
                                                          mapOutlinePanelController.getPanel()));
    }
  }
}
