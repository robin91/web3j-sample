package com.ethjava.nft;

import com.ethjava.ColdWallet;
import com.ethjava.nft.erc721.ERC721;
import com.ethjava.utils.Environment;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 基于ERC20的代币
 */
public class TokenClient {

	private static Web3j web3j;

	private static Admin admin;

	private static String fromAddress = "0x287C7f9Ea470b228f1A006a8E4b056D775d1b5ab";

	private static String contractAddress = "0x8952487fd1dbfca106301bb60ef81e08f2b7f2a2";

	private static String emptyAddress = "0x0000000000000000000000000000000000000000";

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
//		admin = Admin.build(new HttpService(Environment.RPC_URL));


		//balanceOf
//		BigInteger tokenBalance = getTokenBalance(web3j, fromAddress, contractAddress);
//		System.out.println("tokenBalance: " + tokenBalance);
		//name
//		System.out.println(getTokenName(web3j, contractAddress));
		//TokenSymbol
//		System.out.println(getTokenSymbol(web3j, contractAddress));
		//getTokenDecimals
//		System.out.println(getTokenDecimals(web3j, contractAddress));
		//getTokenTotalSupply
//		System.out.println(getTokenTotalSupply(web3j, contractAddress));
		//sendTokenTransaction
		testTokenTransaction(web3j,contractAddress,
				"0x36D249E4a0271E89a33f3a63e483035537455765", fromAddress,
				115,0);

//		testTokenTransaction2(web3j,
//				fromAddress, "fdf100d9100e3d2854ea9637d5e65cb25d5bf4695b7d2db6750f9e11c1307a32",
//				contractAddress, "0x8ca764fE0102991ad24f00E05E19014A2119De6F",
//				115,0);

		web3j.shutdown();

	}

	/**
	 * 查询代币余额
	 */
	public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

		String methodName = "balanceOf";

		List<Type> inputParameters = new ArrayList<>();
		Address address = new Address(fromAddress);
		inputParameters.add(address);

		List<TypeReference<?>> outputParameters = new ArrayList<>();
		TypeReference<Uint256> typeReference = new TypeReference<Uint256>(){

		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);
		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

		EthCall ethCall;
		BigInteger balanceValue = BigInteger.ZERO;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			balanceValue = (BigInteger) results.get(0).getValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return balanceValue;
	}

	/**
	 * 查询代币名称
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenName(Web3j web3j, String contractAddress) {
		String methodName = "name";
		String name = null;
		String fromAddr = emptyAddress;
		List<Type> inputParameters = new ArrayList<>();

		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			name = results.get(0).getValue().toString();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 查询代币符号
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static String getTokenSymbol(Web3j web3j, String contractAddress) {
		String methodName = "symbol";
		String symbol = null;
		String fromAddr = emptyAddress;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			symbol = results.get(0).getValue().toString();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return symbol;
	}

	/**
	 * 查询代币精度
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static int getTokenDecimals(Web3j web3j, String contractAddress) {
		String methodName = "decimals";
		String fromAddr = emptyAddress;
		int decimal = 0;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			decimal = Integer.parseInt(results.get(0).getValue().toString());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return decimal;
	}

	/**
	 * 查询代币发行总量
	 *
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
		String methodName = "totalSupplyww";
		String fromAddr = emptyAddress;
		BigInteger totalSupply = BigInteger.ZERO;
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();

		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
		};
		outputParameters.add(typeReference);

		Function function = new Function(methodName, inputParameters, outputParameters);

		String data = FunctionEncoder.encode(function);
		Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

		EthCall ethCall;
		try {
			ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			totalSupply = (BigInteger) results.get(0).getValue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return totalSupply;
	}

	/**
	 * token转账
	 * @param web3j
	 * @param contractAddress
	 * @return
	 */
	private static void testTokenTransaction(Web3j web3j, String contractAddress, String fromAddressStr, String toAddress, double amount, int decimals) {

		BigInteger nonce;
			EthGetTransactionCount ethGetTransactionCount = null;
		try {
			ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddressStr, DefaultBlockParameterName.PENDING).send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ethGetTransactionCount == null) return;
		nonce = ethGetTransactionCount.getTransactionCount();
		System.out.println("nonce " + nonce);
//		BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
//		BigInteger gasLimit = BigInteger.valueOf(60000);
		BigInteger gasPrice = new DefaultGasProvider().getGasPrice();
		BigInteger gasLimit = new DefaultGasProvider().getGasLimit();
		BigInteger value = BigInteger.ZERO;
		//token转账参数
		String methodName = "transferFrom";

		Address fromAddress = new Address(fromAddressStr);
		Address tAddress = new Address(toAddress);
		Uint256 tokenId = new Uint256(115L);

		List<Type> inputParameters = new ArrayList<>();
		inputParameters.add(fromAddress);
		inputParameters.add(tAddress);
		inputParameters.add(tokenId);

		List<TypeReference<?>> outputParameters = new ArrayList<>();

		Function function = new Function(methodName, inputParameters, outputParameters);
		String data = FunctionEncoder.encode(function);

		String signedData;
		try {
			signedData = ColdWallet.signTransaction(nonce, gasPrice, gasLimit, contractAddress, value, data, ChainId.RINKEBY, "");
			if (signedData != null) {
				EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
				System.out.println(ethSendTransaction.getTransactionHash());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void testTokenTransaction2(Web3j web3j, String fromAddress, String privateKey, String contractAddress, String toAddress, double amount, int decimals) {

		Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);

		ERC721 erc721 = ERC721.load(contractAddress,web3j, credentials, new DefaultGasProvider());
		RemoteCall<BigInteger> remoteCall = erc721.balanceOf(fromAddress);
		try {
			BigInteger balanceOf = remoteCall.send();
			System.out.println(balanceOf);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RemoteCall<TransactionReceipt> remoteCall2 = erc721.transferFrom(fromAddress, "0x8ca764fE0102991ad24f00E05E19014A2119De6F", new BigInteger("115"), new BigInteger("0"));
		try {
			TransactionReceipt transactionReceipt = remoteCall2.send();
			System.out.println(transactionReceipt.getTransactionHash());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void batchReq(){
		Credentials credentials = Credentials.create(Environment.PRIVATE_KEY);
		ERC721 erc721 = ERC721.load(contractAddress,web3j, credentials, new DefaultGasProvider());




		web3j.newBatch().add(web3j.ethBlockNumber());
	}


	Credentials credentials;
	{
		credentials = Credentials.create("私钥");
	}

	/**
	 * 需要支付gas的方法
	 * @throws Exception
	 */
	public void setName() throws Exception {

		Function function = new Function(
				"setName",
				Arrays.asList(new Utf8String("Tom")),
				Collections.emptyList());
		BigInteger nonce = getNonce(credentials.getAddress());
		String encodedFunction = FunctionEncoder.encode(function);

		BigInteger gasLimit = new BigInteger("300000");
		RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, gasLimit, contractAddress, encodedFunction);

		org.web3j.protocol.core.methods.response.EthSendTransaction response =
				web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
						.sendAsync()
						.get();

		String transactionHash = response.getTransactionHash();
		System.out.println(transactionHash);
	}

	/**
	 * 需要支付gas和value的合约方法调用
	 * @throws Exception
	 */
	public void payETH() throws Exception {
		BigInteger nonce = getNonce(credentials.getAddress());
		Function function = new Function("payETH",
				Collections.EMPTY_LIST,
				Collections.EMPTY_LIST);

		String functionEncode = FunctionEncoder.encode(function);
		BigInteger value = new BigInteger("200");
		// 与不需要支付的value的方法调用，差别就在于多传一个eth数量的value参数
		RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE,DefaultGasProvider.GAS_LIMIT, contractAddress, value,functionEncode);
		org.web3j.protocol.core.methods.response.EthSendTransaction response =
				web3j.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials)))
						.sendAsync()
						.get();
		String transactionHash = response.getTransactionHash();
		System.out.println(transactionHash);

	}

	private BigInteger getNonce(String address) throws Exception {
		EthGetTransactionCount ethGetTransactionCount =
				web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
						.sendAsync()
						.get();
		return ethGetTransactionCount.getTransactionCount();
	}

	/**
	 * 合约返回的值是一个地址数组的情况怎么写。
	 *
	 * 如下，是我的一个例子
	 *
	 * @throws Exception
	 */
	public void callContractTransaction() throws Exception {
		Function function = new Function(
				"getComponents",
				Collections.EMPTY_LIST,
				Arrays.asList(new TypeReference<DynamicArray<Address>>(){}));

		String encodedFunction = FunctionEncoder.encode(function);
		org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
				Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
				DefaultBlockParameterName.LATEST)
				.sendAsync().get();
//		Assert.isNull(response.getError(),"callContractTransaction error");

		List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
		for (Type result : results) {
			System.out.println(result.getValue());
		}
	}
}
