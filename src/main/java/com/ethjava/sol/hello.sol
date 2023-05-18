// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

abstract contract Storage {

    uint256 number;

    function store(uint256 num) public {
        number = num;
    }

    function retrieve() public view returns (uint256){
        return number;
    }
}