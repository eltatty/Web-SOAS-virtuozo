package SOAS3;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OntologyHandler {
	/*
	 * This class handles the ontology with apache jena. It contains methods to create an ontology model, read ontology file, and pose 
	 * any query.
	 */
	private OntModel ontModel;
	private String NS ;

	public OntologyHandler() {
		ontModel=null;
		NS = PrintUtil.egNS;
	}



	/* CreateOntologyModel*/
	public void createOntologyModel(OntModelSpec spec,Model model)
	{
		this.ontModel= ModelFactory.createOntologyModel(spec, model);
	}

	/*Read the Ontology*/
	public OntModel readOntologyFile(String address, String type)
	{
		this.ontModel.read(address,type);
		return ontModel;
	}

	/* Get Model */
	public OntModel getOntModel() {
		return ontModel;
	}
	/*SetModel */
	public void setOntModel(OntModel ontModel) {
		this.ontModel = ontModel;
	}
	
	public void writeOntology(String productFile)
	{
		
	}
	/* Pose a query*/
	public List<String> QueryCreator(String the_query,String[] columnsName)
	{

		Query query = QueryFactory.create(the_query);
		QueryExecution qexec = QueryExecutionFactory.create(query,ontModel);
		ResultSet results = qexec.execSelect();
		List<String> answer = new ArrayList<String>();
		while(results.hasNext())
		{
			QuerySolution t = results.nextSolution();
			String queryAnswer="";
			for (String col : columnsName){
			RDFNode x = t.get(col);
			String s = x.toString();
			queryAnswer+=s+",";
			}
			answer.add(queryAnswer);
		}
		qexec.close();
		return answer;
	}

	/* Print Ontology*/
	public void PrintOntology()
	{
		ontModel.write(System.out, "TURTLE");
	}

	/* Print Ontology to file */
	public String PrintOntologyToFile(String productName){
//		File folder = new File( "nemo/Products");
		File folder = new File( "Products");
		String base = "https://www.example.com/service/#" + productName;
		Date date = new Date();
		//SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		String newProduct = "product_" + productName +"_" + formatter.format(date)+ ".xml";
		String product = folder.getAbsolutePath() + "/" + newProduct;
		try{
			OutputStream fileOut = new FileOutputStream(product);
			ontModel.write(fileOut, "RDF/XML", base);
		}catch (Exception e) {
			e.getStackTrace();
		}

		return newProduct;
	}

}
