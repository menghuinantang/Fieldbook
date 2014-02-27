/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package com.efficio.fieldbook.web.nursery.bean;

import java.io.IOException;
import java.util.List;

import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;

import com.csvreader.CsvWriter;

/**
 * This code was copied from CIMMYT's Fieldbook Code. 
 */
public class CSVOziel {


    //private JTable jTableObservations;
    //private JList lista;
    //private DefaultListModel listModel = new DefaultListModel();
	
	private List<MeasurementRow> observations;
	private List<MeasurementVariable> headers;
	
    private String stringTraitToEvaluate = "GY";

    public CSVOziel(Workbook workbook) {
    	this.headers = workbook.getMeasurementDatasetVariables();
    	this.observations = workbook.getObservations();
    }
    /*public CSVOziel(JTable jTableObservations, JList list) {
        this.jTableObservations = jTableObservations;
        this.lista = list;
    }*/

    public void writeColums(CsvWriter csvOutput, int columnas) {
        for (int i = 0; i < columnas; i++) {
            String cad = null;
            try {
                csvOutput.write(cad);
            } catch (IOException ex) {
                System.out.println("ERROR AL GENERAR COLUMNS CSV");
            }

        }
    }

    public void writeRows(CsvWriter csvOutput, int rows) {
        try {
            for (int j = 0; j < rows; j++) {
                writeColums(csvOutput, 129);
                csvOutput.endRecord();
            }
        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR ROWS CSV " + ex);
        }
    }
/*
    public void writeTraits(CsvWriter csvOutput) {
        try {
            listModel = (DefaultListModel) lista.getModel();
            int tot = listModel.size();

            for (int i = 0; i < tot; i++) {
                String cadena = listModel.getElementAt(i).toString();
                int espacio = cadena.indexOf("(");
                String valor = cadena.substring(0, espacio - 1).trim();
                csvOutput.write(valor);
            }


            writeColums(csvOutput, 104 - tot);
            csvOutput.write("IBFB");


        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR TRAITS CSV " + ex);
        }
    }
*/
    public void writeTraitsFromObservations(CsvWriter csvOutput) {
        try {
            int tot = 0;

            for (MeasurementVariable variate : this.headers) {
                csvOutput.write(variate.getName());
                tot++;
            }

            writeColums(csvOutput, 104 - tot);
            csvOutput.write("IBFB");


        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR TRAITS CSV " + ex);
        }
    }
/*
    public void writeTraitsR(CsvWriter csvOutput) {
        try {

            listModel = (DefaultListModel) lista.getModel();
            int tot = listModel.size();

            for (int i = 0; i < tot; i++) {
                String cadena = listModel.getElementAt(i).toString();
                int espacio = cadena.indexOf("(");
                String valor = cadena.substring(0, espacio - 1).trim();
                if (!valor.equals(stringTraitToEvaluate)) {
                    if(valor.isEmpty()){
                    valor=".";
                }
                    csvOutput.write(valor);
                }
            }

        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR TRAITS CSV " + ex);
        }
    }

    public void writeTraitsR(CsvWriter csvOutput, ObservationsTableModel tableModel) {
        try {
            int tot = tableModel.getVariateList().size();

            for (int i = 0; i < tot; i++) {
               String valor = tableModel.getVariateList().get(i).getVariateName();
                if (!valor.equals(stringTraitToEvaluate)) {
                    
                if(valor.isEmpty()){
                    valor=".";
                }
                csvOutput.write(valor);
                }
            }

        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR TRAITS CSV " + ex);
        }
    }
*/
    /*
    public void writeDATA(CsvWriter csvOutput) {
        //int total = observations.size();
        //int tot = listModel.size();
        try {
            //for (int i = 0; i < total; i++) {
        	for (MeasurementRow row : this.observations) {

                csvOutput.write(row.getMeasurementDataValue(label));
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("REP")).toString());
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("BLOCK")).toString());
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("PLOT")).toString());
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("ENTRY")).toString());
                writeColums(csvOutput, 2);
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("DESIG")).toString());
                writeColums(csvOutput, 15);
                csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn("GID")).toString());
                writeColums(csvOutput, 2);

                for (int j = 0; j < tot; j++) {
                    String cadena = listModel.getElementAt(j).toString();
                    int espacio = cadena.indexOf("(");
                    String valor = cadena.substring(0, espacio - 1).trim();


                    try {
                        csvOutput.write(modeloFiltro.getValueAt(i, modeloFiltro.findColumn(valor)).toString());
                    } catch (NullPointerException ex) {
                        String cad = null;
                        csvOutput.write(cad);
                    }
                }

                writeColums(csvOutput, 104 - tot);
                csvOutput.write("END");
                csvOutput.endRecord();
            }
        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR DATA CSV " + ex);
        }

    }
    */
   

    public void writeDATA(CsvWriter csvOutput) {
        //int total = tableModel.getRowCount();
        //int tot = tableModel.getVariateList().size();
    	int tot = 0;

//        int plotColumn = tableModel.getHeaderIndex(ObservationsTableModel.PLOT);
//        int entryColumn = tableModel.getHeaderIndex(ObservationsTableModel.ENTRY);
//        int designColumn = tableModel.getHeaderIndex(ObservationsTableModel.DESIG);
//        int gidColumn = tableModel.getHeaderIndex(ObservationsTableModel.GID);
        try {
            for (MeasurementRow row : this.observations) {

                csvOutput.write("1");
                csvOutput.write("1");
                csvOutput.write("1");
                String plot = getValueByIdInRow(TermId.PLOT_NO.getId(), row);
                if (plot == null || "".equals(plot.trim())) {
                	plot = getValueByIdInRow(TermId.PLOT_NNO.getId(), row);
                }
                csvOutput.write(plot);
                csvOutput.write(getValueByIdInRow(TermId.ENTRY_NO.getId(), row));
                writeColums(csvOutput, 2);
                csvOutput.write(getValueByIdInRow(TermId.DESIG.getId(), row));
                writeColums(csvOutput, 15);
                csvOutput.write(getValueByIdInRow(TermId.GID.getId(), row));
                writeColums(csvOutput, 2);

/*
                for (int j = 0; j < tot; j++) {
                    String valor = tableModel.getVariateList().get(j).getVariateName();
                   if (!valor.equals(stringTraitToEvaluate)) {
                        try {
                            csvOutput.write(tableModel.getValueAt(i, tableModel.findColumn(valor)).toString());
                        } catch (NullPointerException ex) {
                            String cad = ".";
                            csvOutput.write(cad);
                        }
                    }

                }
*/
                writeColums(csvOutput, 104 - tot);
                csvOutput.write("END");
                csvOutput.endRecord();
            }
        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR DATA CSV " + ex);
        }

    }
/*
    public void writeDATAR(CsvWriter csvOutput, DefaultTableModel modeloFilter) {

        int total = modeloFilter.getRowCount();
        int tot = listModel.size();

        try {


            for (int i = 0; i < total; i++) {

                csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn("NURSERY")).toString());
                csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn("REP")).toString());
                csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn("BLOCK")).toString());
                csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn("ENTRY")).toString());
                try {
                    csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn(stringTraitToEvaluate)).toString());
                } catch (NullPointerException ex) {
                    String cad = null;
                    csvOutput.write(cad);
                }


                for (int j = 0; j < tot; j++) {
                    String cadena = listModel.getElementAt(j).toString();
                    int espacio = cadena.indexOf("(");
                    String valor = cadena.substring(0, espacio - 1).trim();

                    if (!valor.equals(stringTraitToEvaluate)) {
                        try {
                            csvOutput.write(modeloFilter.getValueAt(i, modeloFilter.findColumn(valor)).toString());
                        } catch (NullPointerException ex) {
                            String cad = null;
                            csvOutput.write(cad);
                        }
                    }

                }

                csvOutput.endRecord();
            }
        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR DATA CSV FOR R" + ex);
        }

    }

    public void writeDATAR(CsvWriter csvOutput, ObservationsTableModel tableModel) {
        int total = tableModel.getRowCount();
        int tot = tableModel.getVariateList().size();

        //  int trialColumn = tableModel.getHeaderIndex(ObservationsTableModel.NURSERY);
        // int repColumn = tableModel.getHeaderIndex(ObservationsTableModel.REPLICATION);
        // int blockColumn = tableModel.getHeaderIndex(ObservationsTableModel.BLOCK);
        int plotColumn = tableModel.getHeaderIndex(ObservationsTableModel.PLOT);
        int entryColumn = tableModel.getHeaderIndex(ObservationsTableModel.ENTRY);
        int designColumn = tableModel.getHeaderIndex(ObservationsTableModel.DESIG);
        int gidColumn = tableModel.getHeaderIndex(ObservationsTableModel.GID);


        try {


            for (int i = 0; i < total; i++) {

                // for nursery assume alwas values are:
                // loc = 1, replication = 1 and block =1 an
                 csvOutput.write("1"); // lock
                 csvOutput.write("1"); // replication
                 csvOutput.write("1"); // blocki
                
                csvOutput.write(tableModel.getValueAt(i, entryColumn).toString());
              try {
                   csvOutput.write(tableModel.getValueAt(i, tableModel.findColumn(stringTraitToEvaluate)).toString());
                } catch (NullPointerException ex) {
                    String cad = ".";
                    
                    csvOutput.write(cad);
                }


                for (int j = 0; j < tot; j++) {
                    String valor = tableModel.getVariateList().get(j).getVariateName();

                     if (!valor.equals(stringTraitToEvaluate)) {
                        try {
                            csvOutput.write(tableModel.getValueAt(i, tableModel.findColumn(valor)).toString());
                        } catch (NullPointerException ex) {
                            String cad = ".";
                            csvOutput.write(cad);
                        }
                    }

                }

                csvOutput.endRecord();
            }
        } catch (IOException ex) {
            System.out.println("ERROR AL GENERAR DATA CSV FOR R" + ex);
        }

    }
*/
/*
    @SuppressWarnings("unchecked")
    public void readDATA(File file) {

        ArrayList titulos = new ArrayList();
        DefaultTableModel modelo = (DefaultTableModel) jTableObservations.getModel();
        //ObservationsTableModel modelo =  (ObservationsTableModel)jTableObservations.getModel();
        System.out.println("TENEMOS: " + dameTotalDatos(file));

        try {
            CsvReader csvReader = new CsvReader(file.toString());
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();

            if (headers[headers.length - 1].equals("IBFB")) {
                System.out.println("ES DEL IBFB");
            } else {
                System.out.println("NO ES DEL IBFB");
            }

            for (int i = 26; i < headers.length - 1; i++) {
                String titulo = headers[i];
                if (!titulo.equals("")) {
                    System.out.println(titulo);
                    titulos.add(titulo);
                }
            }

            for (int i = 0; i < 23; i++) {
                csvReader.skipRecord();

            }

            System.out.println("TENEMOS traits: " + titulos.size());


            int myrow = 0;
            while (csvReader.readRecord()) {
                String dataOfTraits = "";

                String trial = csvReader.get("Trial");
                String rep = csvReader.get("Rep");
                String block = csvReader.get("Block");
                String plot = csvReader.get("Plot");
                String entry = csvReader.get("Entry");
                String ped = csvReader.get("BreedersPedigree1");
                String gid = csvReader.get("GID");


                for (int i = 0; i < titulos.size(); i++) {

                    String head = titulos.get(i).toString();

                    int col = buscaCol(head);

                    if (col >= 0) {
                        String data = csvReader.get(head);
                        modelo.setValueAt(data, myrow, col);

                        dataOfTraits = dataOfTraits + " " + data;
                    } else {
                        modelo.addColumn(head);

                        col = buscaCol(head);
                        String data = csvReader.get(head);
                        modelo.setValueAt(data, myrow, col);

                        dataOfTraits = dataOfTraits + " " + data;

                    }


                }

                myrow++;

                System.out.println(trial + " " + rep + " " + block + " " + plot + " " + entry + " " + ped + " " + gid + dataOfTraits);
            }

            csvReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("FILE NOT FOUND. readDATAcsv. " + ex);

        } catch (IOException e) {
            System.out.println("IO EXCEPTION. readDATAcsv. " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public void readDATAnew(File file) {

        ArrayList titulos = new ArrayList();
        ObservationsTableModel modelo = (ObservationsTableModel) jTableObservations.getModel();
        //DefaultTableModel modelo = (DefaultTableModel) jTableObservations.getModel();
        int add = 0;
        String before = "";
        String actual = "";

        try {
            CsvReader csvReader = new CsvReader(file.toString());
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();

            if (headers[headers.length - 1].equals("IBFB")) {
                System.out.println("ES DEL IBFB");
            } else {
                System.out.println("NO ES DEL IBFB");
            }

            for (int i = 26; i < headers.length - 1; i++) {
                String titulo = headers[i];
                if (!titulo.equals("")) {
                    System.out.println(titulo);
                    titulos.add(titulo);
                }
            }

            for (int i = 0; i < 23; i++) {
                csvReader.skipRecord();
            }

            System.out.println("TENEMOS traits: " + titulos.size());

            int myrow = 0;
            while (csvReader.readRecord()) {

                String dataOfTraits = "";
                before = actual;
                String trial = csvReader.get("Trial");
                String rep = csvReader.get("Rep");
                String block = csvReader.get("Block");
                String plot = csvReader.get("Plot");
                String entry = csvReader.get("Entry");
                String ped = csvReader.get("BreedersPedigree1");
                String gid = csvReader.get("GID");

                System.out.println("TRIAL : --->>>" + trial);
                System.out.println("PLOT : --->>>" + plot);

                actual = entry;

                if (before.equals(entry)) {
                    add++;
                } else {
                    add = 0;
                }


                try {
                    myrow = findRow(Integer.parseInt(plot));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    System.out.println("ERROR AL IMPORTAR FIELDLOG FILE");
                    return;
                }

                for (int i = 0; i < titulos.size(); i++) {
                    String head = titulos.get(i).toString();
                    int col = buscaCol(head);
                    if (col >= 0) {
                        String data = csvReader.get(head);
                        modelo.setValueAt(data, myrow + add, col);
                        dataOfTraits = dataOfTraits + " " + data;
                    } else {
                        //modelo.addColumn(head);
                        col = buscaCol(head);
                        String data = csvReader.get(head);
                        modelo.setValueAt(data, myrow + add, col);
                        dataOfTraits = dataOfTraits + " " + data;
                    }
                }

                //  myrow++;
                //System.out.println(trial + " " + rep + " " + block + " " + plot + " " + entry + " " + ped + " " + gid + dataOfTraits);
            }
            csvReader.close();
            modelo.fireTableDataChanged();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("FILE NOT FOUND. readDATAcsv. " + ex);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO EXCEPTION. readDATAcsv. " + e);
        }
    }
*/
/*    private int findRow(int plot) {
        int row = 0;

        ObservationsTableModel modelo = (ObservationsTableModel) jTableObservations.getModel();
        //  int colTrial = modelo.getHeaderIndex(ObservationsTableModel.NURSERY);
        int colplot = modelo.getHeaderIndex(ObservationsTableModel.PLOT);


        for (int i = 0; i < modelo.getRowCount(); i++) {
            // int trialFromRow = Integer.parseInt(modelo.getValueAt(i, colTrial).toString()); 
            int plotFromRow = Integer.parseInt(modelo.getValueAt(i, colplot).toString());
            if (plot == plotFromRow) {
                row = i;
                break;
            }
        }

        return row;
    }

    public int dameTotalDatos(File file) {
        int total = 0;
        try {
            CsvReader csvReader = new CsvReader(file.toString());

            for (int i = 0; i < 24; i++) {
                csvReader.skipRecord();
            }

            while (csvReader.readRecord()) {
                total++;
            }
        } catch (IOException ex) {
            System.out.println("ERROR EN CONTAR REGISTROS CSV READER");
            total = 0;
        }

        return total;
    }

    public int dameTotalColumnas(File file) {
        int total = 0;
        try {
            CsvReader csvReader = new CsvReader(file.toString());

            total = csvReader.getHeaderCount();


        } catch (IOException ex) {
            System.out.println("ERROR EN CONTAR REGISTROS CSV READER");
            total = 0;
        }

        return total;
    }

    public boolean isValid(File file) {
        boolean isvalid = false;
        try {
            CsvReader csvReader = new CsvReader(file.toString());
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();

            if (headers[headers.length - 1].equals("IBFB")) {
                isvalid = true;
            } else {
                isvalid = false;
            }
        } catch (IOException ex) {
        }
        return isvalid;
    }

    private int buscaCol(String head) {
        int col = -1;

        for (int i = 0; i < this.jTableObservations.getColumnCount(); i++) {
            if (head.equals(this.jTableObservations.getColumnName(i))) {
                col = i;
            }
        }
        return col;
    }
    
    public void DefineTraitToEvaluate(String stringTraitToEval) {
        this.stringTraitToEvaluate=stringTraitToEval;
    }
*/
    private String getValueByIdInRow(int termId, MeasurementRow row) {
    	String label = null;
    	if (this.headers != null && !this.headers.isEmpty()) {
    		for (MeasurementVariable header : headers) {
    			if (header.getTermId() == termId) {
    				label = header.getName();
    				break;
    			}
    		}
    	}
    	if (label != null && observations != null && !observations.isEmpty()) {
   			return row.getMeasurementDataValue(label);
    	}
    	return null;
    }
}