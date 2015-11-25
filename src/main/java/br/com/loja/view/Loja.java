package br.com.loja.view;

import br.com.loja.model.produto.Produto;
import br.com.loja.model.produto.ProdutoDAO;
import br.com.loja.model.modeloDeProduto.ModeloDeProduto;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.List;

import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;


public class Loja {

    public static void main(String[] args) throws JRException, FileNotFoundException {
        

        Produto produto = new Produto();
        produto.setNome("Sapatenis");
        produto.setMarca("Democrata");

        ModeloDeProduto m1 = new ModeloDeProduto("Marron/Branco", produto);
        ModeloDeProduto m2 = new ModeloDeProduto("Blue/Grenn", produto);
        ModeloDeProduto m3 = new ModeloDeProduto("Preto/Vermelho", produto);

        List<ModeloDeProduto> modeloDeProdutos = new ArrayList<>();
        modeloDeProdutos.add(m1);
        modeloDeProdutos.add(m2);
        modeloDeProdutos.add(m3);

        produto.setModeloDeProdutos(modeloDeProdutos);

        ProdutoDAO produtoDAO = new ProdutoDAO();
        produtoDAO.create(produto);

        //ClienteDAO clienteDAO = new ClienteDAO();
        List<Produto> produtos = produtoDAO.buscarTodos();

//        String arquivo = "loja.jrxml";

//        JasperCompileManager.compileReportToFile(arquivo);

        try {

            gerarRelatorio(produtos);

        } catch (NullPointerException e) {

            System.out.println("Exception" + e);

        }
    }

    public static void gerarRelatorio(List<Produto> objetos) throws FileNotFoundException {

        JasperViewer viewer = null;

        InputStream arquivoJasper;

        //caminho de onde esta o arquivo jasper
        arquivoJasper = new FileInputStream("Report/produto.jasper");

        try {

            JRBeanCollectionDataSource fonte = new JRBeanCollectionDataSource(objetos, false);

            JasperPrint impressao = JasperFillManager.fillReport(arquivoJasper, null, fonte);

            if (impressao.getPages() != null && !impressao.getPages().isEmpty()) {

                viewer = new JasperViewer(impressao, false);
                viewer.setVisible(true);
                viewer.toFront();
                viewer.show();
            } else {

                JOptionPane.showMessageDialog(null, "DOCUMENTO NÃO CONTÉM PÁGINAS");

            }

        } catch (Exception e) {

        }

    }
}
