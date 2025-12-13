package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

public final class ExportPanel extends JPanel implements QctFileAware {
    private final JButton exportPngButton = new JButton("PNG...");

    private final QctFileService qctFileService;

    private QctFile qctFile;

    public ExportPanel(final QctFileService qctFileService) {
        super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[grow]", "[fill, grow]"));
        this.qctFileService = Objects.requireNonNull(qctFileService);
        setBorder(BorderFactory.createTitledBorder("Export"));

        exportPngButton.addActionListener(_ -> exportPng());
        exportPngButton.setEnabled(false);

        add(exportPngButton, "wrap");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        qctFileService.bind(this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        qctFileService.unbind(this);
    }

    @Override
    public void onQctFile(final QctFile qctFile) {
        this.qctFile = qctFile;
        exportPngButton.setEnabled(true);
    }

    private void exportPng() {
        // TODO
    }
}
