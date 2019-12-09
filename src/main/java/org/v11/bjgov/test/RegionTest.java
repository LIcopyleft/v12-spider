package org.v11.bjgov.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import javafx.beans.property.Property;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.BeanUtils;
import org.v11.utils.ReadFile;
import org.v11.utils.WriteFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author tengchao.li
 * @description
 * @date 2019/10/15
 */
public class RegionTest {
    private static final Double Distance = 10d;

    public static void main(String args[]) {

        String path = "D:\\myspace\\v11-spider\\city.json";
        String file = readJsonFile(path);
        // file = JSON.toJSONString(file);
        //   List<Region> list = JSON.parseObject(file, List.class);
        List<Region> obj = JSON.parseObject(file, new TypeReference<List<Region>>() {
        });
        List<Map<String , List<double[]>>> li = new LinkedList();
        List<Region> regions = loopDimension(obj);
        List<Region> region = regions.get(0).getChild();
        int count = 0;
        List<Region> noLngList = new LinkedList<Region>();
        for (Region reg : region) {
            //县级list
            List<Region> regChild = reg.getChild();
            for (Region r : regChild) {
                List<Double> lnglat = r.getLnglat();
                if (lnglat.size() == 0) {
                    Region rg = new Region();
                    BeanUtils.copyProperties(r, rg);
                    noLngList.add(rg);
                    // System.out.println(++count);
                    continue;
                }

                Double lng = lnglat.get(0);
                Double lat = lnglat.get(1);
            //    GlobalCoordinates source = new GlobalCoordinates(lat, lng);
              //  Random ran = new Random();

                List<double[]> neighPosition = findNeighPosition(lng, lat);
                Map map = new HashMap();

                map.put(r.getRegionid(),neighPosition);
            //    map.put("region",r);
                li.add(map);

            }


        }

      /*  GlobalCoordinates source = new GlobalCoordinates(29, 106);
        GlobalCoordinates target = new GlobalCoordinates(30, 106);


        double meter1 = getDistanceMeter(source, target, Ellipsoid.Sphere);
*/

      /*  System.out.println(JSON.toJSONString(regions));
        System.out.println(JSON.toJSONString(noLngList));*/
        System.out.println(JSON.toJSONString(li));

        String s = JSON.toJSONString(li);
        List list = new LinkedList();
        list.add(s);
  //      String s1 = JSON.toJSONString(li);

       WriteFile.write(list,"./regionLatLng.json");
        //取出县级list

        /*String s = randomLonLat(28.0001, 29.0001, 105.0001, 106.0001);
        GlobalCoordinates source = new GlobalCoordinates(29, 106);
        String[] split = s.split(",");
        GlobalCoordinates target = new GlobalCoordinates(Double.valueOf(split[1]), Double.valueOf(split[0]));
        double meter1 = getDistanceMeter(source, target, Ellipsoid.Sphere);
        System.out.println(meter1);*/
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

    public static List<double[]> findNeighPosition(double longitude, double latitude) {
        List<double[]> list = new LinkedList();
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米  
        double dis = Distance;//10千米距离  

        while (list.size() < 40) {
            double dlng = 2 * Math.asin(Math.sin(dis / (2 * r)) / Math.cos(latitude * Math.PI / 180));
            dlng = dlng * 180 / Math.PI;//角度转为弧度  
            double dlat = dis / r;
            dlat = dlat * 180 / Math.PI;
            double minlat = latitude - dlat;
            double maxlat = latitude + dlat;
            double minlng = longitude - dlng;
            double maxlng = longitude + dlng;

            Random random = new Random();
            double dLat = maxlat - minlat;
            double dLng = maxlng - minlng;
           /* long lat = (long) Math.floor(dLat);
            long lng = (long) Math.floor(dLng);*/
            double lat = new BigDecimal(dLat).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            double lng = new BigDecimal(dLng).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            double i = random.nextDouble();
            double lt = i * lat;
            double lg = i * lng;

            double latr = minlat + lt;
            double lngr = minlng + lg;

            GlobalCoordinates source = new GlobalCoordinates(latitude, longitude);
            GlobalCoordinates target = new GlobalCoordinates(latr, lngr);

            double distanceMeter = getDistanceMeter(source, target, Ellipsoid.Sphere);
            System.out.println(distanceMeter+"米");
            if (distanceMeter < Distance * 1000) {
                Map m = new HashMap();
                double[] d = new double[2];
                d[0] = new BigDecimal(latr).setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                d[1] = new BigDecimal(lngr).setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                list.add(d);
            }
        }
        return list;

     /*   //    String hql = "from Property where longitude>=? and longitude =<? and latitude>=? latitude=<? and state=0";
        Object[] values = {minlng, maxlng, minlat, maxlat};

    //    List<Property> list = find(hql, values);
        return null;*/
    }


    /**
 //    * @param MinLon：最小经度 MaxLon： 最大经度   MinLat：最小纬度   MaxLat：最大纬度    type：设置返回经度还是纬度
     * @return
     * @throws
     * @Title: randomLonLat
     * @Description: 在矩形内随机生成经纬度
     */
    /*public static String randomLonLat(double MinLon, double MaxLon, double MinLat, double MaxLat) {
        Random random = new Random();
        BigDecimal db = new BigDecimal(Math.random() * (MaxLon - MinLon) + MinLon);
        String lon = db.setScale(6, BigDecimal.ROUND_HALF_UP).toString();// 小数后6位
        db = new BigDecimal(Math.random() * (MaxLat - MinLat) + MinLat);
        String lat = db.setScale(6, BigDecimal.ROUND_HALF_UP).toString();
        *//*if (type.equals("Lon")) {
            return lon;
        } else {
            return lat;
        }*//*


        return lat + "," + lon;
    }*/


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

    static class Region {
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


    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
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


    public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid) {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }

}
