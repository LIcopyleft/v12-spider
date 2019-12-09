package org.v11.bjgov.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.v11.utils.ReadFile;
import org.v11.utils.WriteFile;

import java.io.*;
import java.util.*;

/**
 * @author tengchao.li
 * @description
 * @date 2019/10/15
 */
public class Test {
    private static final String URL = "http://rsj.beijing.gov.cn/integralpublic/settlePerson/tablePage";

    public static void main(String args[]) {
/*
        HttpGet httpGet = new HttpGet(URL);
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
       *//* if(RefererString == null){
            httpGet.setHeader("Referer", LOGIN_URL);
        }
        else {
            httpGet.setHeader("Referer", RefererString);
        }*//*
        httpGet.setHeader("Content-Type", "text/html;charset=GBK");*/
     /*   String path = "D:\\ideaProject\\bmap\\src\\main\\resources\\static\\img\\graph\\chart";
        ArrayList<String> list = new ArrayList();
        getAllFileName(path,list);

        WriteFile.write(list,"./aaaaaaaa.txt");
        System.out.println(list.toArray());*/
        String path = "D:\\myspace\\v11-spider\\city.json";
        String file = readJsonFile(path);
       // file = JSON.toJSONString(file);
        List<Region> list = JSON.parseObject(file, List.class);
        List<Region> obj = JSON.parseObject(file, new TypeReference<List<Region>>(){});

        List<Region> regions = loopDimension(obj);



        System.out.println(JSON.toJSONString(regions));

        //取出县级list

    }
    public static List<Region> loopDimension(List<Region> treeNodes) {
        List<Region> trees = new ArrayList<Region>();
        for (Region treeNode : treeNodes) {
            if (treeNode.getParentid() != null && treeNode.getParentid().equals("0")) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    public static Region findChildren(Region treeNode, List<Region> treeNodes) {
        //treeNode.setChildren(new ArrayList<Region>());
        treeNode.setChild(null);
        for (Region it : treeNodes) {
            if (treeNode.getRegionid().equals(it.getParentid())) {
                if (treeNode.getChild() == null) {
                    treeNode.setChild(new ArrayList<Region>());
                }
                treeNode.getChild().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }
    static class  Region{
        private String regionid;
        private String name;
        private String ordernumber;
        private String parentid;
        private List<Double> lnglat;
        private int isleaf;
        private List<Region> child;

        public List<Region> getChild() {
            return child;
        }

        public void setChild(List<Region> child) {
            this.child = child;
        }

        public String getRegionid() {
            return regionid;
        }

        public void setRegionid(String regionid) {
            this.regionid = regionid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrdernumber() {
            return ordernumber;
        }

        public void setOrdernumber(String ordernumber) {
            this.ordernumber = ordernumber;
        }

        public String getParentid() {
            return parentid;
        }

        public void setParentid(String parentid) {
            this.parentid = parentid;
        }

        public List<Double> getLnglat() {
            return lnglat;
        }

        public void setLnglat(List<Double> lnglat) {
            this.lnglat = lnglat;
        }

        public int getIsleaf() {
            return isleaf;
        }

        public void setIsleaf(int isleaf) {
            this.isleaf = isleaf;
        }
    }

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param fileNameList 存放文件名称的list
     * @param path         文件夹的路径
     * @return
     */
    public static void getAllFileName(String path, ArrayList<String> fileNameList) {
        //ArrayList<String> files = new ArrayList<String>();
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
//              System.out.println("文     件：" + tempList[i]);
                //fileNameList.add(tempList[i].toString());
                fileNameList.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
                getAllFileName(tempList[i].getAbsolutePath(), fileNameList);
            }
        }
        return;
    }




    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
