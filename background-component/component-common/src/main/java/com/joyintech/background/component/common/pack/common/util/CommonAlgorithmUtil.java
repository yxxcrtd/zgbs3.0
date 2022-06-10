/*
 * 文件名：CommonAlgorithmUtil.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：杨智
 * 修改时间：2018年3月23日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */

package com.joyintech.background.component.common.pack.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.joyintech.background.component.common.pack.common.grid.GridRowDto;

/**
 * 通用算法包
 * 
 * @author 杨智
 * @version 1.0 2018年3月23日
 * @see CommonAlgorithmUtil
 * @since 1.0
 */
public class CommonAlgorithmUtil {

    /**
     * 将list转为森林
     * 
     * @param list 树状表格列表
     * @param <T> 业务实体类型
     * @return 森林
     * @see
     * @since 1.0
     */
    public static <T extends GridRowDto> List<TreeNode<T>> gridToTree(List<T> list) {
        Set<String> allIdSet = list.stream().map(T::getId).collect(Collectors.toSet());
        List<TreeNode<T>> result = list.stream().filter(item -> {
            String pid = item.getParentId();
            if (StringUtil.isEmpty(pid) || !allIdSet.contains(pid)) {
                return true;
            }
            return false;
        }).map(item -> new TreeNode<T>(item, null)).collect(Collectors.toList());
        Map<String, List<TreeNode<T>>> iterateMap = result.stream().collect(
            Collectors.groupingBy(item -> item.id));
        while (!iterateMap.isEmpty()) {
            ConcurrentHashMap<String, List<TreeNode<T>>> childrenMap = new ConcurrentHashMap<>();
            iterateMap.entrySet().forEach(entry -> {
                childrenMap.put(entry.getKey(),
                    list.stream().filter(
                        item -> StringUtil.equals(item.getParentId(), entry.getKey())).map(
                            item -> new TreeNode<T>(item, entry.getValue().get(0))).collect(
                                Collectors.toList()));
            });
            childrenMap.forEach((k, v) -> {
                iterateMap.get(k).get(0).children = v;
            });
            iterateMap.clear();
            iterateMap.putAll(
                childrenMap.values().stream().reduce(new ArrayList<TreeNode<T>>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                }).stream().collect(Collectors.groupingBy(item -> item.id)));
        }
        return result;
    }

    /**
     * 深度优先遍历树，同时提供层序数(根为0)
     * 
     * @param forest 森林
     * @param action 迭代处理函数，接收层序和业务数据
     * @param <T> 业务实体类型
     * @see
     * @since 1.0
     */
    public static <T extends GridRowDto> void treeDepthFirstTraversal(List<TreeNode<T>> forest,
                                                                      BiConsumer<Integer, T> action) {
        Stack<TreeNode<T>> stack = new Stack<>();
        stack.addAll(forest);
        int currentDepth = 0;
        ConcurrentHashMap<Integer, AtomicInteger> levelNodesRemain = new ConcurrentHashMap<>();
        levelNodesRemain.put(currentDepth, new AtomicInteger(forest.size()));
        while (!stack.isEmpty()) {
            TreeNode<T> node = stack.pop();
            levelNodesRemain.get(currentDepth).getAndDecrement();
            action.accept(currentDepth, node.data);
            if (!StringUtil.isEmptyList(node.children)) {
                currentDepth++ ;
                levelNodesRemain.put(currentDepth, new AtomicInteger(node.children.size()));
                stack.addAll(node.children);
            }
            if (levelNodesRemain.get(currentDepth).get() == 0) {
                currentDepth-- ;
            }
        }
    }

    /**
     * 广度优先遍历树，同时提供层序数(根为0)
     * 
     * @param forest 森林
     * @param action 迭代处理函数，接收层序和业务数据
     * @param <T> 业务实体类型
     * @see
     * @since 1.0
     */
    public static <T extends GridRowDto> void treeBreadthFirstTraversal(List<TreeNode<T>> forest,
                                                                        BiConsumer<Integer, T> action) {
        ConcurrentLinkedQueue<TreeNode<T>> queue = new ConcurrentLinkedQueue<>();
        queue.addAll(forest);
        int currentDepth = 0;
        ConcurrentHashMap<Integer, AtomicInteger> levelNodesRemain = new ConcurrentHashMap<>();
        levelNodesRemain.put(currentDepth, new AtomicInteger(forest.size()));
        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.poll();
            levelNodesRemain.get(currentDepth).getAndDecrement();
            action.accept(currentDepth, node.data);
            if (!StringUtil.isEmptyList(node.children)) {
                AtomicInteger branchNum = new AtomicInteger(0);
                AtomicInteger atomicInteger = levelNodesRemain.putIfAbsent(currentDepth + 1,
                    branchNum);
                if (atomicInteger != null) {
                    atomicInteger.updateAndGet(i -> i + node.children.size());
                }
                else {
                    branchNum.updateAndGet(i -> i + node.children.size());
                }
                queue.addAll(node.children);
            }
            if (levelNodesRemain.get(currentDepth).get() == 0) {
                currentDepth++ ;
            }
        }
    }

    /**
     * 获取一个List的全排列，每一份中间结果都是列表的一次浅表复制
     * 
     * @param list 列表
     * @param comparator 比较器
     * @param action 迭代处理器，接收全排列序号(0开始编号)和元素，返回是否继续迭代
     * @param <T> 数组元素类型
     * @see
     * @since 1.0
     */
    public static <T> void permutation(List<T> list, Comparator<T> comparator,
                                       BiFunction<Integer, List<T>, Boolean> action) {
        List<T> wrapperList = list.stream().sorted(comparator).collect(Collectors.toList());
        boolean nextFlag = action.apply(0, wrapperList.stream().collect(Collectors.toList()));
        for (int count = 0; nextFlag; count++ ) {
            int i = wrapperList.size() - 1;
            for (; i > 0
                   && comparator.compare(wrapperList.get(i), wrapperList.get(i - 1)) <= 0; i-- ) {
            }
            if (i == 0) {
                return;
            }
            int j = i + 1;
            for (; j < wrapperList.size()
                   && comparator.compare(wrapperList.get(i - 1), wrapperList.get(j)) < 0; j++ ) {
            }
            Collections.swap(wrapperList, i - 1, j - 1);
            List<T> reversedSubList = IntStream.iterate(wrapperList.size() - 1,
                idx -> idx - 1).limit(wrapperList.size() - (long)i).boxed().map(
                    idx -> wrapperList.get(idx)).collect(Collectors.toList());
            for (int k = i; k < wrapperList.size(); k++ ) {
                wrapperList.set(k, reversedSubList.get(k - i));
            }
            nextFlag = action.apply(count, wrapperList.stream().collect(Collectors.toList()));
        }
    }

    public static class TreeNode<T extends GridRowDto> {

        public String id;

        public T data;

        public TreeNode<T> parent;

        public List<TreeNode<T>> children;

        public TreeNode(T item, TreeNode<T> parent) {
            id = item.getId();
            this.parent = parent;
            data = item;
        }
    }
}
