package com.example.demo;

import SOAS3.SoasMapper;
import openllet.jena.PelletReasonerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HomeController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    public List<String> graphs = new ArrayList<String>();

    @RequestMapping("/")
    public String welcome(Model model){
        return "welcome";
    }

    @RequestMapping("/upload")
    public String upload(Model model, @RequestParam("files") MultipartFile[] files){
        StringBuilder fileNames = new StringBuilder();
        String current = null;
        String newProduct = null;
        for(MultipartFile file : files){
            if (FilenameUtils.getExtension(file.getOriginalFilename()).equals("yaml")){
                current = FilenameUtils.removeExtension(file.getOriginalFilename());
                Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
                fileNames.append(file.getOriginalFilename());
                try {
                    Files.write(fileNameAndPath, file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(current!=null) {
            //Change TestServices/ to uploadDirectory
            SoasMapper control = new SoasMapper(uploadDirectory, "openApi3.ttl", "shacl.ttl", PelletReasonerFactory.THE_SPEC, null, "RDF/XML");
            newProduct = control.PrintOntologyToFile(current);
        }


        // Delete uploads
        try {
            FileUtils.cleanDirectory(new File(uploadDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File folder = new File("Products");
        File[] product_files = folder.listFiles();



        // Curl Test
        //curl --digest --user dba:dba --verbose --url "http://localhost:8890/sparql-graph-crud-auth?graph-uri=http://localhost:8890/conv" -T books.ttl
        String[] command = { "curl", "--digest", "--user", "dba:dba", "--verbose", "--url", "http://virtuoso_box:8890/sparql-graph-crud-auth?graph-uri=http://example/"+current, "-T", "Products/"+newProduct };
        curlItLikeBeckam(command);


        model.addAttribute("products", product_files);
        model.addAttribute("msg", "Successfully uploaded files " + fileNames.toString());
        //return "uploadstatusview";
        return "redirect:/ontologies";
    }

    @RequestMapping("/ontologies")
    public String ontologies(Model model){
        File folder = new File("Products");
        File[] product_files = folder.listFiles();
        model.addAttribute("products", product_files);
        return "uploadstatusview";
    }

    @RequestMapping("/query")
    public String query(Model model){
        Pattern pattern = Pattern.compile("(?<=(<uri>))(\\w|\\d|\\n|[().,\\-:;@#$%^&*\\[\\]\"'+–/\\/®°⁰!?{}|`~]| )+?(?=(</uri>))");
        String message = "SELECT DISTINCT ?g WHERE  { GRAPH ?g {?s ?p ?o} } ORDER BY  ?g";
        String[] command = {"curl", "-F", "query=\""+message+"\"", "http://virtuoso_box:8890/sparql"};

        String result = curlItLikeBeckam(command);
        Matcher matcher = pattern.matcher(result);

        graphs.clear();

        while(matcher.find()){
            graphs.add(matcher.group());
        }

        for (int i=0; i<5;i++){
            graphs.remove(graphs.size()-1);
        }
        model.addAttribute("graphs", graphs);
        return "queries";
    }

    @RequestMapping("/sparql")
    public String query(Model model, @RequestParam("message") String message){
        String result = null;
        String messageChanged = message.replaceAll("\"", "\\\\\"");
        String[] command = {"curl", "-F", "query=\""+messageChanged+"\"", "http://virtuoso_box:8890/sparql"};
        result = curlItLikeBeckam(command);
        model.addAttribute("answer", result);
        model.addAttribute("graphs", graphs);
        return "queries";
    }

    @RequestMapping("/file/{fileName}")
    @ResponseBody
    public void show(@PathVariable("fileName") String fileName, HttpServletResponse response){
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream("Products" +"/"+ fileName);
            int len;
            byte[] buf = new byte[1024];
            while((len = fis.read(buf)) > 0) {
                bos.write(buf,0,len);
            }
            bos.close();
            response.flushBuffer();
        }
        catch(IOException e) {
            e.printStackTrace();

        }
    }

    public String curlItLikeBeckam(String[] command){
        String result = null;
        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        try{
            p = process.start();
            BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



}