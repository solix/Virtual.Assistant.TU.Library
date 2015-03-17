package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import models.*;
import play.mvc.*;
import play.data.Form;

import java.io.*;
import java.util.*;

import static play.libs.Json.toJson;

import controllers.routes;

/**
 * This controller will handle Mendeley related actions
 */
public class MendeleyData extends Controller {

    public static void syncMendeleyFolders(Person person){
        Logger.debug("SYNCING MENDELEY FOLDERS");
        List<Project> projects = PersonData.findActiveProjects();
        Map<String, String> foldernames = new HashMap<String, String>();
        try {
            foldernames = getMendeleyFolderNames(person.mendeleyToken);
        } catch(IOException e){
            Logger.debug("IOException: " + e.getMessage());
        }
        String folder_formatted = "";
        ObjectNode folderdata = new ObjectMapper().createObjectNode();
        for(Project project : projects){
            folder_formatted = project.folder + " (" + project.id + ")";
            if(!foldernames.containsKey(folder_formatted)){
                folderdata.put("name", folder_formatted);
                try {
                    Logger.debug("CREATING NEW FOLDER: " + folderdata);
                    createMendeleyFolder(folderdata, person.mendeleyToken);
                } catch(IOException e){
                    Logger.debug("IOException: " + e.getMessage());
                }
            }
        }
    }

    public static MendeleyDocument createMendeleyDocument(Person person, String document_id, String folder){
        Logger.debug("CREATING MENDELEY DOCUMENT FROM ID: " + document_id);
        String documentdata = "";
        try{
            documentdata = getMendeleyDocument(person.mendeleyToken, document_id);
        } catch(IOException e){
            Logger.debug("IOException: " + e.getMessage());
        }
        return MendeleyDocument.create(person.id, Json.parse(documentdata), folder);
    }

    public static Person clearMendeleyData(Person person){
        List<MendeleyDocument> mendeley_documents = MendeleyDocument.find.where().eq("person", person).findList();
        for(MendeleyDocument mendeley_document : mendeley_documents){
            mendeley_document.delete();
        }
        person.update();
        return person;
    }

    public static Person updateMendeleyData(Person person){
        Logger.debug("UPDATING MENDELEY");
        syncMendeleyFolders(person);
        person = clearMendeleyData(person);
        Map<String, String> folderdata = new HashMap<String, String>();
        try{
            folderdata = getMendeleyFolderNames(person.mendeleyToken);
        } catch(IOException e){
            Logger.debug("IOException: " + e.getMessage());
        }
        Set<String> foldernames = folderdata.keySet();
        List<String> document_ids = new ArrayList<String>();
        String document = "";
        for(String foldername : foldernames) {
            try{
                document_ids = getMendeleyDocumentIds(person.mendeleyToken, folderdata.get(foldername));
            } catch(IOException e){
                Logger.debug("IOException: " + e.getMessage());
            }
            for(String document_id : document_ids){
                createMendeleyDocument(person, document_id, foldername);
            }
            person.update();
        }
        return person;
    }

    public static boolean hasMendeleyProjectFolder(Person person, String folder_title){
        Boolean result = false;
        Map<String, String> foldernames = new HashMap<String, String>();
        try {
            foldernames = getMendeleyFolderNames(person.mendeleyToken);
        } catch(IOException e){
            Logger.debug("IOException: " + e.getMessage());
        }
        if(foldernames.containsKey(folder_title))
            result = true;
        return result;
    }

    public static Map<String, String> getMendeleyFolderNames(String token) throws IOException {

        Map<String, String> result = new HashMap<String, String>();
        String folderdata = "";

        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/folders",
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-folder.1+json");

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        folderdata = br.readLine();
        br.close();
        JsonNode jsondata = Json.parse(folderdata);
        for(JsonNode folder : jsondata){
            result.put(folder.get("name").asText(), folder.get("id").asText());
        }
        Logger.debug("MENDELEY FOLDER NAMES RESULT: " + result);
        return result;
    }

    public static List<String> getMendeleyDocumentIds(String token, String folder_id) throws IOException {
        Logger.debug("FOLDER ID: " + folder_id);
        List<String> documents = new ArrayList<String>();
        String documentdata = "";

        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/folders/" + folder_id + "/documents",
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-document.1+json");

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        documentdata = br.readLine();
        Logger.debug("IDs in folder " + folder_id + ": " + documentdata);
        br.close();
        JsonNode jsondata = Json.parse(documentdata);
        Logger.debug("JSONDATA: " + jsondata.toString());
        for(JsonNode document : jsondata){
            documents.add(document.get("id").asText());
        }
        Logger.debug("MENDELEY DOCUMENT IDS RESULT: " + documents);
        return documents;
    }

    public static String getMendeleyDocument(String token, String document_id) throws IOException {

        String document = "";

        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/documents/" + document_id,
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-document.1+json");

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        document = br.readLine();
        br.close();
        Logger.debug("MENDELEY DOCUMENT AS STRING: " + document);
        return document;
    }

    public static void createMendeleyFolder(JsonNode data, String token) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/folders",
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-folder.1+json",
                "-H", "Content-Type: application/vnd.mendeley-folder.1+json",
                "--data-binary", Json.stringify(data));

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        String strLine="";
        while ((strLine=br.readLine())!=null) {
            Logger.debug("STRLINE: " + strLine);
        }
        br.close();
    }

    public static boolean hasFullLibrary(Person person, Long pid){
        List<MendeleyDocument> documents = ProjectData.findAllMendeleyDocuments(pid);
        Boolean result = true;
        for(MendeleyDocument document : documents){
            if(MendeleyDocument.find.where().eq("title", document.title).eq("person", person).findList().size() == 0)
                result = false;
        }
        return result;
    }

    public static Result exportMendeleyLibrary(Long pid){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null) {
            List<MendeleyDocument> documents = ProjectData.findAllMendeleyDocuments(pid);
            for (MendeleyDocument document : documents) {
                if (MendeleyDocument.find.where().eq("title", document.title).eq("person", person).findList().size() == 0) {
                    try {
                        if(!getMendeleyFolderNames(person.mendeleyToken).containsKey(document.folder)){
                            syncMendeleyFolders(person);
                        }
                        exportDocumentToMendeley(Json.parse(document.nodeData), person.mendeleyToken);
                    } catch (IOException e) {
                        Logger.debug("IOException: " + e.getMessage());
                    }
                }
            }
            return Authentication.OAuth("mendeley");
        }else{
            return redirect(routes.Authentication.login());
        }
    }

    public static void exportDocumentToMendeley(JsonNode data, String token) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/documents",
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-document.1+json",
                "-H", "Content-Type: application/vnd.mendeley-document.1+json",
                "--data-binary", Json.stringify(data));

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        String strLine="";
        while ((strLine=br.readLine())!=null) {
            Logger.debug("STRLINE: " + strLine);
        }
        br.close();
    }

    public static List<Person> findSharedPersons(String mendeley_title){
        List<Person> result = new ArrayList<Person>();
        List<MendeleyDocument> mendeley_docs = MendeleyDocument.find.where().eq("title", mendeley_title).findList();
        for(MendeleyDocument mendeley_doc : mendeley_docs){
            result.add(mendeley_doc.person);
        }
        return result;
    }

}