/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class ProjectForkJoin extends JFrame {

    private ChatServer server;
    private ChatClient client;
    private String name;
    private ChatFrame frame;

    private ProjectForkJoin(ChatServer server, String clientName) throws RemoteException {
        super("Client Name - " + clientName);
        this.server = server;
        this.name = clientName;
        //window
        Dimension dimWindow = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(dimWindow.width / 10, dimWindow.height / 10, dimWindow.width / 2, dimWindow.height / 2);
        initComponents();
        //chat frame
        frame = new ChatFrame(server, name);
        this.setupEvents();
    }

    private void setupEvents() {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                logout();
            }
        });
    }

    private void logout() {
        try {
            this.server.logout(this.name);
        } catch (Exception se) {
            se.printStackTrace();
        }
        this.dispose();
        System.exit(0);
    }

    //
    //Solucion 
    private void operacionMatricesSecuencialSuma(CreateMatrix matrix) {
        long inicio;

        //Secuencial
        inicio = System.currentTimeMillis();
        MatrixAdd matrixAdd = new MatrixAdd(matrix.getMatrixA(), matrix.getMatrixB());
        this.lblSecuAnsSuma.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));

        for (int i = 0; i < matrixAdd.getResultados().length; i++) {
            txtAreaSumaSec.append(Arrays.toString(matrixAdd.getResultados()[i]) + "\r\n");
        }
    }

    private void operacionMatricesSecuencialMulti(CreateMatrix matrix) {
        long inicio;
        //Secuencial
        inicio = System.currentTimeMillis();
        MatrixMulti matrixMulti = new MatrixMulti(matrix.getMatrixA(), matrix.getMatrixB());
        this.lblSecuAnsMulti.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));

        for (int i = 0; i < matrixMulti.getResultados().length; i++) {
            txtAreaMultiSec.append(Arrays.toString(matrixMulti.getResultados()[i]) + "\r\n");
        }
    }

    private void operacionMatrizConcurrenciaSuma(CreateMatrix matrix) {
        long inicio;
        //Concurrencia
        MatrixAddForkJoin forkAdd = new MatrixAddForkJoin();
        //MatrixAddForkJoin2 forkAdd = new MatrixAddForkJoin2();
        inicio = System.currentTimeMillis();
        //forkAdd.matrixAdd(matrix.getMatrixA(), matrix.getMatrixB());
        forkAdd.matrixAdd(matrix.getMatrixA(), matrix.getMatrixB(), 100, 0, 0);
        this.lblConcuAnsSuma.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));
        for (int i = 0; i < forkAdd.getResultados().length; i++) {
            txtAreaSumaCon.append(Arrays.toString(forkAdd.getResultados()[i]) + "\r\n");
        }
    }

    private void operacionMatrizConcurrenciaMulti(CreateMatrix matrix) {
        long inicio;
        //Concurrencia
        MatrixMultiForkJoin forkMulti = new MatrixMultiForkJoin();
        inicio = System.currentTimeMillis();
        forkMulti.matrixMulti(matrix.getMatrixA(), matrix.getMatrixB(), 0, 0);
        this.lblConcuAnsMulti.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));
        for (int i = 0; i < forkMulti.getResultados().length; i++) {
            txtAreaMultiCon.append(Arrays.toString(forkMulti.getResultados()[i]) + "\r\n");
        }
    }

    private void operacionMatrizParalelaSuma(CreateMatrix matrix, String[] clientNames, int numeroSaltos) {
        long inicio, impresion;
        int dividirAbajo = numeroSaltos, dividirArriba = 0, posicion = 0;
        //dentro pc
        MatrixAddForkJoin forkAdd = new MatrixAddForkJoin();
        inicio = System.currentTimeMillis();

        forkAdd.matrixAdd(matrix.getMatrixA(), matrix.getMatrixB(), 100, dividirArriba, dividirAbajo);

        impresion = System.currentTimeMillis();
        for (int i = dividirArriba; i < dividirAbajo; i++) {
            txtAreaSumaParalelo.append(Arrays.toString(forkAdd.getResultados()[i]) + "\r\n");
        }
        inicio = System.currentTimeMillis() - impresion + inicio;
        impresion = 0;
        
        //fuera de pc
        for (String clientName : clientNames) {
            try {
                posicion++;
                dividirArriba = ((posicion) * numeroSaltos);
                dividirAbajo = ((posicion + 1) * numeroSaltos);

                frame.chatArea.append("Sent matrix to: " + clientName + "\r\n");

                server.sendMatrix(name, clientName, matrix.getMatrixA(), matrix.getMatrixB(), "Add", dividirArriba, dividirAbajo);
                server.getMatrix();

                impresion = System.currentTimeMillis();
                for (int i = dividirArriba; i < dividirAbajo; i++) {
                    txtAreaSumaParalelo.append(Arrays.toString(server.getMatrix()[i]) + "\r\n");
                }
                inicio = System.currentTimeMillis() - impresion + inicio;
                impresion = 0;
                
            } catch (RemoteException re) {
                re.printStackTrace();
                frame.chatArea.append("Send matrix failes for: " + clientName + "\r\n");
            }
        }

        this.lblParaleloAnsSuma.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));
    }

    private void operacionMatrizParalelaMulti(CreateMatrix matrix, String[] clientNames, int numeroSaltos) {
        long inicio, impresion;
        int dividirAbajo = numeroSaltos, dividirArriba = 0, posicion = 0;
        
        //dentro de la pc
        MatrixMultiForkJoin forkMulti = new MatrixMultiForkJoin();
        inicio = System.currentTimeMillis();

        forkMulti.matrixMulti(matrix.getMatrixA(), matrix.getMatrixB(), dividirArriba, dividirAbajo);

        impresion = System.currentTimeMillis();
        for (int i = dividirArriba; i < dividirAbajo; i++) {
            txtAreaMultiParalelo.append(Arrays.toString(forkMulti.getResultados()[i]) + "\r\n");
        }
        inicio = System.currentTimeMillis() - impresion + inicio;
        impresion = 0;
        //fuera de la pc
        for (String clientName : clientNames) {
            try {
                posicion++;
                dividirArriba = ((posicion) * numeroSaltos);
                dividirAbajo = ((posicion + 1) * numeroSaltos);

                frame.chatArea.append("Sent matrix to: " + clientName + "\r\n");
                //Solucionar Matriz
                server.sendMatrix(name, clientName, matrix.getMatrixA(), matrix.getMatrixB(), "Multi", dividirArriba, dividirAbajo);
                server.getMatrix();
                
                impresion = System.currentTimeMillis();
                for (int i = dividirArriba; i < dividirAbajo; i++) {
                    txtAreaMultiParalelo.append(Arrays.toString(server.getMatrix()[i]) + "\r\n");
                }
                inicio = System.currentTimeMillis() - impresion + inicio;
                impresion=0;
                
            } catch (RemoteException re) {
                re.printStackTrace();
                frame.chatArea.append("Send matrix failes for: " + clientName + "\r\n");
            }
        }
        
        this.lblParaleloAnsMulti.setText(Integer.toString((int) (System.currentTimeMillis() - inicio)));
    }

    private void clearTxt() {
        this.txtAreaMatrizB.setText("");
        this.txtAreaMatrizA.setText("");
        this.txtAreaMultiCon.setText("");
        this.txtAreaMultiSec.setText("");
        this.txtAreaSumaCon.setText("");
        this.txtAreaSumaSec.setText("");
        this.txtAreaMultiParalelo.setText("");
        this.txtAreaSumaParalelo.setText("");
        this.lblConcuAnsMulti.setText("0");
        this.lblConcuAnsSuma.setText("0");
        this.lblSecuAnsMulti.setText("0");
        this.lblSecuAnsSuma.setText("0");
        this.lblParaleloAnsMulti.setText("0");
        this.lblParaleloAnsSuma.setText("0");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        PanelDatosGral = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        spnMatrixValue = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        chkMulti = new javax.swing.JCheckBox();
        chkSuma = new javax.swing.JCheckBox();
        jCombTipo = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        PanelPrincipalPanel = new javax.swing.JPanel();
        PanelPrincipal = new javax.swing.JLabel();
        lblSecuAnsSuma = new javax.swing.JLabel();
        lblConcuAnsSuma = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblSecuAnsMulti = new javax.swing.JLabel();
        lblConcuAnsMulti = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaMultiCon = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaMultiSec = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAreaSumaCon = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtAreaSumaSec = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtAreaMatrizA = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtAreaMatrizB = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        btnIniciar = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtAreaMultiParalelo = new javax.swing.JTextArea();
        lblParaleloAnsMulti = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblParaleloAnsSuma = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtAreaSumaParalelo = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("Tiempo de ejecucion en ms");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Concurrencia +");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 153));
        jLabel5.setText("Tiempo de ejecucion en ms:");
        jLabel5.setName(""); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 153));
        jLabel6.setText("Secuencial +");

        PanelDatosGral.setBackground(new java.awt.Color(0, 143, 255));
        PanelDatosGral.setForeground(new java.awt.Color(250, 250, 250));
        PanelDatosGral.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        PanelDatosGral.setMinimumSize(new java.awt.Dimension(400, 230));
        PanelDatosGral.setPreferredSize(new java.awt.Dimension(400, 300));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Tamaño de las Matrices:");

        spnMatrixValue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        spnMatrixValue.setModel(new javax.swing.SpinnerNumberModel(3, 1, 10000, 1));

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Multiplicacion:");

        jLabel8.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Suma:");

        chkMulti.setBackground(new java.awt.Color(0, 143, 255));

        chkSuma.setBackground(new java.awt.Color(0, 143, 255));
        chkSuma.setSelected(true);

        jCombTipo.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jCombTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Secuencial", "Concurrencia", "Paralelo" }));
        jCombTipo.setSelectedIndex(2);

        jLabel15.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Elige el Metodo:");
        jLabel15.setToolTipText("");

        javax.swing.GroupLayout PanelDatosGralLayout = new javax.swing.GroupLayout(PanelDatosGral);
        PanelDatosGral.setLayout(PanelDatosGralLayout);
        PanelDatosGralLayout.setHorizontalGroup(
            PanelDatosGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosGralLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMulti)
                .addGap(35, 35, 35)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkSuma)
                .addGap(69, 69, 69)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnMatrixValue, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCombTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelDatosGralLayout.setVerticalGroup(
            PanelDatosGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosGralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDatosGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelDatosGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnMatrixValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jCombTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addGroup(PanelDatosGralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(chkMulti)
                        .addComponent(chkSuma)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelPrincipalPanel.setBackground(new java.awt.Color(34, 34, 34));

        PanelPrincipal.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        PanelPrincipal.setForeground(new java.awt.Color(242, 242, 242));
        PanelPrincipal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PanelPrincipal.setText("Solucion de Multiplicacion de Matrices  Cuadradas (Secuencial vs Concurrencia y Paralelo)");
        PanelPrincipal.setFocusable(false);
        PanelPrincipal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PanelPrincipal.setInheritsPopupMenu(false);
        PanelPrincipal.setPreferredSize(new java.awt.Dimension(210, 64));

        javax.swing.GroupLayout PanelPrincipalPanelLayout = new javax.swing.GroupLayout(PanelPrincipalPanel);
        PanelPrincipalPanel.setLayout(PanelPrincipalPanelLayout);
        PanelPrincipalPanelLayout.setHorizontalGroup(
            PanelPrincipalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PanelPrincipalPanelLayout.setVerticalGroup(
            PanelPrincipalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPrincipalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblSecuAnsSuma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSecuAnsSuma.setForeground(new java.awt.Color(0, 0, 153));
        lblSecuAnsSuma.setText("0");

        lblConcuAnsSuma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblConcuAnsSuma.setForeground(new java.awt.Color(0, 102, 102));
        lblConcuAnsSuma.setText("0");

        jLabel9.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 102, 102));
        jLabel9.setText("Concurrencia *");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 153));
        jLabel10.setText("Tiempo de ejecucion en ms:");

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 153));
        jLabel11.setText("Secuencial *");

        lblSecuAnsMulti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSecuAnsMulti.setForeground(new java.awt.Color(0, 0, 153));
        lblSecuAnsMulti.setText("0");

        lblConcuAnsMulti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblConcuAnsMulti.setForeground(new java.awt.Color(0, 102, 102));
        lblConcuAnsMulti.setText("0");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 102, 102));
        jLabel12.setText("Tiempo de ejecucion en ms");

        txtAreaMultiCon.setColumns(20);
        txtAreaMultiCon.setRows(5);
        jScrollPane1.setViewportView(txtAreaMultiCon);

        txtAreaMultiSec.setColumns(20);
        txtAreaMultiSec.setRows(5);
        jScrollPane3.setViewportView(txtAreaMultiSec);

        txtAreaSumaCon.setColumns(20);
        txtAreaSumaCon.setRows(5);
        jScrollPane4.setViewportView(txtAreaSumaCon);
        txtAreaSumaCon.getAccessibleContext().setAccessibleDescription("");

        txtAreaSumaSec.setColumns(20);
        txtAreaSumaSec.setRows(5);
        jScrollPane5.setViewportView(txtAreaSumaSec);

        txtAreaMatrizA.setEditable(false);
        txtAreaMatrizA.setColumns(20);
        txtAreaMatrizA.setRows(5);
        jScrollPane6.setViewportView(txtAreaMatrizA);

        jLabel13.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setText("Matriz A");

        txtAreaMatrizB.setEditable(false);
        txtAreaMatrizB.setColumns(20);
        txtAreaMatrizB.setRows(5);
        jScrollPane7.setViewportView(txtAreaMatrizB);

        jLabel14.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 0, 0));
        jLabel14.setText("Matriz B");

        btnIniciar.setBackground(new java.awt.Color(255, 0, 0));
        btnIniciar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnIniciar.setForeground(new java.awt.Color(255, 255, 255));
        btnIniciar.setText("Calcular");
        btnIniciar.setMaximumSize(new java.awt.Dimension(120, 40));
        btnIniciar.setMinimumSize(new java.awt.Dimension(120, 40));
        btnIniciar.setPreferredSize(new java.awt.Dimension(120, 40));
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        txtAreaMultiParalelo.setColumns(20);
        txtAreaMultiParalelo.setRows(5);
        jScrollPane8.setViewportView(txtAreaMultiParalelo);

        lblParaleloAnsMulti.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblParaleloAnsMulti.setForeground(new java.awt.Color(153, 0, 153));
        lblParaleloAnsMulti.setText("0");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(153, 0, 153));
        jLabel16.setText("Tiempo de ejecucion en ms:");

        jLabel17.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(153, 0, 153));
        jLabel17.setText("Paralelo * ");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 0, 153));
        jLabel18.setText("Tiempo de ejecucion en ms:");

        jLabel19.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(153, 0, 153));
        jLabel19.setText("Paralelo + ");

        lblParaleloAnsSuma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblParaleloAnsSuma.setForeground(new java.awt.Color(153, 0, 153));
        lblParaleloAnsSuma.setText("0");

        txtAreaSumaParalelo.setColumns(20);
        txtAreaSumaParalelo.setRows(5);
        jScrollPane9.setViewportView(txtAreaSumaParalelo);

        jMenu1.setText("Open");

        jMenuItem1.setText("Chat");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Logout");
        jMenuItem2.setToolTipText("");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipalPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PanelDatosGral, javax.swing.GroupLayout.DEFAULT_SIZE, 1300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel13)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSecuAnsSuma, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblConcuAnsSuma, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblParaleloAnsSuma, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel14)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addGap(34, 34, 34)
                                .addComponent(lblSecuAnsMulti, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel12)
                                .addGap(10, 10, 10)
                                .addComponent(lblConcuAnsMulti, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblParaleloAnsMulti, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PanelPrincipalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelDatosGral, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(lblSecuAnsSuma)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblConcuAnsSuma)
                                .addComponent(jLabel19)
                                .addComponent(jLabel18)
                                .addComponent(lblParaleloAnsSuma)))))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lblSecuAnsMulti, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel12))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(jLabel16)
                                .addComponent(lblParaleloAnsMulti, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblConcuAnsMulti))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        this.btnIniciar.setEnabled(false);
        clearTxt();
        CreateMatrix matrix = new CreateMatrix((int) spnMatrixValue.getValue());

        for (double[] matrixA : matrix.getMatrixA()) {
            txtAreaMatrizA.append(Arrays.toString(matrixA) + "\r\n");
        }
        for (double[] matrixB : matrix.getMatrixB()) {
            txtAreaMatrizB.append(Arrays.toString(matrixB) + "\r\n");
        }
        switch (jCombTipo.getSelectedIndex()) {
            case 0:
                if (this.chkMulti.isSelected() && this.chkSuma.isSelected()) {
                    operacionMatricesSecuencialMulti(matrix);
                    operacionMatricesSecuencialSuma(matrix);
                } else if (this.chkMulti.isSelected()) {
                    operacionMatricesSecuencialMulti(matrix);
                } else if (this.chkSuma.isSelected()) {
                    operacionMatricesSecuencialSuma(matrix);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Seleciona Una Opcion");
                }
                break;
            case 1:
                if (this.chkMulti.isSelected() && this.chkSuma.isSelected()) {
                    operacionMatrizConcurrenciaMulti(matrix);
                    operacionMatrizConcurrenciaSuma(matrix);
                } else if (this.chkMulti.isSelected()) {
                    operacionMatrizConcurrenciaMulti(matrix);
                } else if (this.chkSuma.isSelected()) {
                    operacionMatrizConcurrenciaSuma(matrix);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Seleciona Una Opcion");
                }
                break;
            case 2:
                //Asignar a las computadoras correspondientes
                String[] clientNames = frame.clientList.getSelectedItems();
                int i = 1,
                 numeroSaltos = 0;

                for (String clientName : clientNames) {
                    i++;
                }
                numeroSaltos = (matrix.getMatrixA().length / i);

                if (this.chkMulti.isSelected() && this.chkSuma.isSelected()) {
                    operacionMatrizParalelaMulti(matrix, clientNames, numeroSaltos);
                    operacionMatrizParalelaSuma(matrix, clientNames, numeroSaltos);
                } else if (this.chkMulti.isSelected()) {
                    operacionMatrizParalelaMulti(matrix, clientNames, numeroSaltos);
                } else if (this.chkSuma.isSelected()) {
                    operacionMatrizParalelaSuma(matrix, clientNames, numeroSaltos);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Seleciona Una Opcion");
                }
                break;
        }

        this.btnIniciar.setEnabled(true);
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        frame.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    public static void main(String args[]) throws Exception {
        String clientName = args[0];
        String host = args[1];
        String chatRoom = args[2];

        //Buscar el servidor
        ChatServer server = (ChatServer) Naming.lookup("rmi://" + host + "/" + chatRoom);
        ProjectForkJoin projectForkJoin = new ProjectForkJoin(server, clientName);
        projectForkJoin.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDatosGral;
    private javax.swing.JLabel PanelPrincipal;
    private javax.swing.JPanel PanelPrincipalPanel;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JCheckBox chkMulti;
    private javax.swing.JCheckBox chkSuma;
    private javax.swing.JComboBox<String> jCombTipo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel lblConcuAnsMulti;
    private javax.swing.JLabel lblConcuAnsSuma;
    private javax.swing.JLabel lblParaleloAnsMulti;
    private javax.swing.JLabel lblParaleloAnsSuma;
    private javax.swing.JLabel lblSecuAnsMulti;
    private javax.swing.JLabel lblSecuAnsSuma;
    private javax.swing.JSpinner spnMatrixValue;
    public javax.swing.JTextArea txtAreaMatrizA;
    public javax.swing.JTextArea txtAreaMatrizB;
    private javax.swing.JTextArea txtAreaMultiCon;
    private javax.swing.JTextArea txtAreaMultiParalelo;
    private javax.swing.JTextArea txtAreaMultiSec;
    private javax.swing.JTextArea txtAreaSumaCon;
    private javax.swing.JTextArea txtAreaSumaParalelo;
    private javax.swing.JTextArea txtAreaSumaSec;
    // End of variables declaration//GEN-END:variables
}
