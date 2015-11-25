package br.com.loja.model.modeloDeProduto;

import br.com.loja.model.produto.Produto;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ModeloDeProduto.class)
public abstract class ModeloDeProduto_ {

	public static volatile SingularAttribute<ModeloDeProduto, Produto> produto;
	public static volatile SingularAttribute<ModeloDeProduto, String> nome;
	public static volatile SingularAttribute<ModeloDeProduto, Long> id;

}

