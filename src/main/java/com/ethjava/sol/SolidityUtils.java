package com.ethjava.sol;

import com.ethjava.sol.gen.Coin;
import com.ethjava.utils.Environment;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * @author mars-peng
 * @version 1.0
 * @date 6/27/22 3:56 PM
 */
public class SolidityUtils {

    /**
     * 利用abi信息 与 bin信息 生成对应的abi,bin文件
     * @param abi 合约编译后的abi信息
     * @param bin 合约编译后的bin信息
     */
    public static void generateABIAndBIN(String abi,String bin,String abiFileName,String binFileName){

        File abiFile = new File("src/main/resources/"+abiFileName);
        File binFile = new File("src/main/resources/"+binFileName);
        BufferedOutputStream abiBos = null;
        BufferedOutputStream binBos = null;
        try{
            FileOutputStream abiFos = new FileOutputStream(abiFile);
            FileOutputStream binFos = new FileOutputStream(binFile);
            abiBos = new BufferedOutputStream(abiFos);
            binBos = new BufferedOutputStream(binFos);
            abiBos.write(abi.getBytes());
            abiBos.flush();
            binBos.write(bin.getBytes());
            binBos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(abiBos != null){
                try{
                    abiBos.close();;
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(binBos != null){
                try {
                    binBos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * 生成合约的java代码
     * 其中 -p 为生成java代码的包路径此参数和 -o 参数配合使用，以便将java文件放入正确的路径当中
     * @param abiFile abi的文件路径
     * @param binFile bin的文件路径
     * @param generateFile 生成的java文件路径
     */
    public static void generateClass(String abiFile,String binFile,String generateFile){
        String[] args = Arrays.asList(
                "-a",abiFile,
                "-b",binFile,
                "-p","",
                "-o",generateFile
        ).toArray(new String[0]);
        Stream.of(args).forEach(System.out::println);
        SolidityFunctionWrapperGenerator.main(args);
    }


    public static void deploy(){

        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));
        Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);
		RemoteCall<Coin> deploy = Coin.deploy(web3j, credentials, new DefaultGasProvider());
		try {
            Coin contract = deploy.send();
            contract.isValid();
            System.out.println(contract.getContractAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

//    public static void call() throws ExecutionException, InterruptedException {
//
//        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));
//        Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);
//
//        Coin coin = Coin.load("0x22b63e08b111a3d75e18f8414fa1a4242a8f6f9c", web3j, credentials, new DefaultGasProvider());
//        RemoteFunctionCall<TransactionReceipt> remoteFunctionCall = coin.mint("0x287C7f9Ea470b228f1A006a8E4b056D775d1b5ab", new BigInteger("1000000"));
//        TransactionReceipt transactionReceipt = remoteFunctionCall.sendAsync().get();
//        String transactionHash = transactionReceipt.getTransactionHash();
//        System.out.println(transactionHash);
//    }

//    public static void call() throws ExecutionException, InterruptedException {
//
//        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));
//        Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);
//
//        Coin coin = Coin.load("0x22b63e08b111a3d75e18f8414fa1a4242a8f6f9c", web3j, credentials, new DefaultGasProvider());
//        RemoteFunctionCall<TransactionReceipt> remoteFunctionCall = coin.store(new BigInteger("1000000"));
//        TransactionReceipt transactionReceipt = remoteFunctionCall.sendAsync().get();
//        String transactionHash = transactionReceipt.getTransactionHash();
//        System.out.println(transactionHash);
//    }

    public static void call() throws ExecutionException, InterruptedException {

        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));
        Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);

        Coin coin = Coin.load("0x22b63e08b111a3d75e18f8414fa1a4242a8f6f9c", web3j, credentials, new DefaultGasProvider());
        RemoteFunctionCall<BigInteger> remoteFunctionCall = coin.retrieve();
        BigInteger transactionReceipt = remoteFunctionCall.sendAsync().get();
        System.out.println(transactionReceipt);
    }

    public static void main(String[] args){

        //生成java类
        String abiFile = "target/classes/Coin.abi";
        String binFile = "target/classes/Coin.bin";
        String generateFile = "src/main/java/com/ethjava/sol/gen/";
        SolidityUtils.generateClass(abiFile,binFile,generateFile);

        //部署合约
//        deploy();

        //调用
//        try {
//            call();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
