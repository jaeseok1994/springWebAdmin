package com.webAdmin.security.domain.model;

/**
 * author:james
 * day:2020-09-10
 */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jonsy
 * id   label   path        level order
 * 1    과일      0           1   1
 * 2    사과      0,1         2   1
 * 3    배       0,1         2   2
 * 4    시드니      0,1,3       3   1
 * 5    Yali      0,1,3       3   2
 */
public class TreeModel {

    private String id;

    private String label;

    private String path="0";  //상위 노드의 경로 및 상위 노드의 ID 경로，용","분리, 0은 상위 노드가 루트 노드임을 의미합니다.

    private int order=1;  //

    private int type;//구분을위한 메뉴 유형

    private String style;//UI 디스플레이

    /** 상태가 비활성화 */
    private boolean disabled;

    private List<? extends TreeModel> childNodes=new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLevel() {
        if(path==null){
            return 1;
        }
        return path.split(",").length;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TreeModel newChildNode(String nodeId, String label, int order){
        TreeModel node=new TreeModel();
        node.path =this.path +","+this.id;
        node.id=nodeId;
        node.order=order;
        node.label=label;
        return node;
    }

    public List<? extends TreeModel> getChildNodes() {
        return childNodes;
    }

    @Override
    public String toString() {
        return label+"-"+ path +"-"+id+"-"+order;
    }

    /***
     * level==트리 구조를 만들기위한 시작 노드로 노드 1 개
     * @param nodes
     * @return
     */
    public static List<? extends TreeModel> buildTree(List<? extends TreeModel> nodes){
        if (isEmpty(nodes)){
            return null;
        }
        List<? extends TreeModel> firstLevels=nodes.stream().filter(node->!node.isDisabled() && node.getLevel()==1).collect(Collectors.toList());
        sortByOrder(firstLevels);
        firstLevels.stream().forEach(node-> setChildren(node,nodes));
        return firstLevels;
    }


    private static  void setChildren(TreeModel currentNode, List<? extends TreeModel> nodeList){
        List<? extends TreeModel> childrens=nodeList.stream().filter(node->(!node.isDisabled() && node.getPath().equals(currentNode.getPath()+","+currentNode.getId()))).collect(Collectors.toList());
        currentNode.childNodes=childrens;
        if (isEmpty(childrens)){
            return;
        }
        sortByOrder(childrens);
        childrens.stream().forEach(node-> setChildren(node,nodeList));

    }

    private static void sortByOrder(List<? extends TreeModel> firstLevels) {
        firstLevels.sort((node1,node2)->Integer.valueOf(node1.getOrder()).compareTo(Integer.valueOf(node2.getOrder())));
    }


    /***
     * 숫자 구조로 노드 정렬
     * @param nodes
     */
    public static void sortByTree(List<? extends TreeModel> nodes) {
        if(isEmpty(nodes)){
            return;
        }
        sortByOrder(nodes);
        nodes.sort((o1, o2) -> (o1.getPath()+","+o1.getId()).compareTo(o2.getPath()+","+o2.getId()));
    }

    private static boolean isEmpty(List nodes) {
        return nodes == null || nodes.isEmpty();
    }


    //노드의 부모-자식 계층 순서로 표시
    private static void printTreeToConsole(List<TreeModel> nodes){
        if (isEmpty(nodes)){
            return;
        }

        sortByTree(nodes);

        nodes.stream().forEach(node->{
            if(node.isDisabled()){
                return;
            }
            for(int i=1;i<node.getLevel();i++){
                System.out.print("\t");
            }
            System.out.println(node);
        });
    }


    //첫 번째 수준에서 시작하여 부모-자식 계층 트리를 재귀 적으로 표시합니다
    private static void printFirstLevelTreeToConsole(List<? extends TreeModel> nodes){
        if (isEmpty(nodes)){
            return;
        }
        nodes.forEach(item->{
            if(item.isDisabled()){
                return;
            }
            for(int i=1;i<item.getLevel();i++){
                System.out.print("\t");
            }
            System.out.println(item);

            printFirstLevelTreeToConsole(item.getChildNodes());
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeModel treeModel = (TreeModel) o;

        return id != null ? id.equals(treeModel.id) : treeModel.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static void main(String[] arg){
        List<TreeModel> nodes=new ArrayList<>();
        TreeModel fruit=new TreeModel();
        fruit.setId("1");
        fruit.setLabel("과일");
        fruit.setOrder(2);
        nodes.add(fruit);

        TreeModel apple=fruit.newChildNode("7","사과",2);
        nodes.add(apple);
        nodes.add(apple.newChildNode("4","붉은 후지",2));
        nodes.add(apple.newChildNode("15","산동 사과",1));

        TreeModel lizi=fruit.newChildNode("e8","배",1);
        nodes.add(lizi);
        nodes.add(lizi.newChildNode("7r7","시드니",1));
        nodes.add(lizi.newChildNode("t31o","Yali",2));

        TreeModel shucai=new TreeModel();
        shucai.setId("a101");
        shucai.setLabel("야채");
        shucai.setOrder(1);
        shucai.setDisabled(true);
        nodes.add(shucai);
        nodes.add(shucai.newChildNode("213","배추",2));
        printTreeToConsole(nodes);

        System.out.println("====================");
        List<? extends TreeModel> tree=TreeModel.buildTree(nodes);
        printFirstLevelTreeToConsole(tree);






    }

}
