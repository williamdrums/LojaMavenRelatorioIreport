/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loja.model.produto;

import br.com.loja.model.modeloDeProduto.ModeloDeProduto;
import br.com.loja.model.produto.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author UFMS
 */
public class ProdutoDAO implements Serializable {

    private final EntityManager em;
//     private final EntityManagerFactory emf;
    
     public ProdutoDAO(){
        this.emf = Persistence.createEntityManagerFactory("LojaPU");
        em = emf.createEntityManager();
    }
    
    public ProdutoDAO(EntityManagerFactory emf, EntityManager em) {
        this.emf = emf;
        this.em = em;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) {
        
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

     public List<Produto> buscarTodos() {

        Query consulta = em.createQuery("select p from Produto p order by p.id");
        return consulta.getResultList();
    }
    
    public void edit(Produto produto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto persistentProduto = em.find(Produto.class, produto.getId());
            List<ModeloDeProduto> modeloDeProdutosOld = persistentProduto.getModeloDeProdutos();
            List<ModeloDeProduto> modeloDeProdutosNew = produto.getModeloDeProdutos();
            List<ModeloDeProduto> attachedModeloDeProdutosNew = new ArrayList<ModeloDeProduto>();
            for (ModeloDeProduto modeloDeProdutosNewModeloDeProdutoToAttach : modeloDeProdutosNew) {
                modeloDeProdutosNewModeloDeProdutoToAttach = em.getReference(modeloDeProdutosNewModeloDeProdutoToAttach.getClass(), modeloDeProdutosNewModeloDeProdutoToAttach.getId());
                attachedModeloDeProdutosNew.add(modeloDeProdutosNewModeloDeProdutoToAttach);
            }
            modeloDeProdutosNew = attachedModeloDeProdutosNew;
            produto.setModeloDeProdutos(modeloDeProdutosNew);
            produto = em.merge(produto);
            for (ModeloDeProduto modeloDeProdutosOldModeloDeProduto : modeloDeProdutosOld) {
                if (!modeloDeProdutosNew.contains(modeloDeProdutosOldModeloDeProduto)) {
                    modeloDeProdutosOldModeloDeProduto.setProduto(null);
                    modeloDeProdutosOldModeloDeProduto = em.merge(modeloDeProdutosOldModeloDeProduto);
                }
            }
            for (ModeloDeProduto modeloDeProdutosNewModeloDeProduto : modeloDeProdutosNew) {
                if (!modeloDeProdutosOld.contains(modeloDeProdutosNewModeloDeProduto)) {
                    Produto oldProdutoOfModeloDeProdutosNewModeloDeProduto = modeloDeProdutosNewModeloDeProduto.getProduto();
                    modeloDeProdutosNewModeloDeProduto.setProduto(produto);
                    modeloDeProdutosNewModeloDeProduto = em.merge(modeloDeProdutosNewModeloDeProduto);
                    if (oldProdutoOfModeloDeProdutosNewModeloDeProduto != null && !oldProdutoOfModeloDeProdutosNewModeloDeProduto.equals(produto)) {
                        oldProdutoOfModeloDeProdutosNewModeloDeProduto.getModeloDeProdutos().remove(modeloDeProdutosNewModeloDeProduto);
                        oldProdutoOfModeloDeProdutosNewModeloDeProduto = em.merge(oldProdutoOfModeloDeProdutosNewModeloDeProduto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = produto.getId();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto;
            try {
                produto = em.getReference(Produto.class, id);
                produto.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produto with id " + id + " no longer exists.", enfe);
            }
            List<ModeloDeProduto> modeloDeProdutos = produto.getModeloDeProdutos();
            for (ModeloDeProduto modeloDeProdutosModeloDeProduto : modeloDeProdutos) {
                modeloDeProdutosModeloDeProduto.setProduto(null);
                modeloDeProdutosModeloDeProduto = em.merge(modeloDeProdutosModeloDeProduto);
            }
            em.remove(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Produto findProduto(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
