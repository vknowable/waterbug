package com.example.waterbug.appstate

// Test networks
val network1 = Network(
    name = "Mainnet",
    chainId = "namada.5f5de2dd1b88cba30586420",
    rpcUrl = "https://rpc.namada.tudues.com",
    indexerUrl = "https://indexer.namada.tududes.com",
    maspIndexerUrl = "https://masp.namada.tududes.com",
)
val network2 = Network(
    name = "Campfire",
    chainId = "campfire-square.ff09671d333707",
    rpcUrl = "https://rpc.campfire.tududes.com",
    indexerUrl = "https://indexer.campfire.tududes.com",
    maspIndexerUrl = "https://masp.campfire.tududes.com",
)
val networkList = listOf(network1, network2)
//////////

// Test assets
val asset1 = Asset(
    name = "NAM",
    address = "tnam1qy440ynh9fwrx8aewjvvmu38zxqgukgc259fzp6h",
    balances = Balance(transparentBalance = 157.002, shieldedBalance = 34.78),
)
val asset2 = Asset(
    name = "OSMO",
    address = "tnam1phks0geerggjk96ezhxclt6r5tdgu3usa5zteyyc",
    balances = Balance(transparentBalance = 22.04, shieldedBalance = 189.9923),
)
val asset3 = Asset(
    name = "ATOM",
    address = "tnam1phzvlar06m0rtjjv7n8qx8ny8j8aexayhyq98r7s",
    balances = Balance(transparentBalance = 12.348, shieldedBalance = 45.0001),
)
val asset1b = Asset(
    name = "NAM",
    address = "tnam1qy440ynh9fwrx8aewjvvmu38zxqgukgc259fzp6h",
    balances = Balance(transparentBalance = 14.765, shieldedBalance = 356.212),
)
val asset2b = Asset(
    name = "transfer/channel-0/uosmo",
    address = "tnam1phks0geerggjk96ezhxclt6r5tdgu3usa5zteyyc",
    balances = Balance(transparentBalance = 122.056, shieldedBalance = 12.43),
)
val asset1c = Asset(
    name = "NAM",
    address = "tnam1qy440ynh9fwrx8aewjvvmu38zxqgukgc259fzp6h",
    balances = Balance(transparentBalance = 32.00, shieldedBalance = 64.43),
)
val assetList1 = listOf(asset1, asset2, asset3)
val assetList2 = listOf(asset1b, asset2b)
val assetList3 = listOf(asset1c)
//////////

// Test accounts
val account1 = Account(
    alias = "account 1",
    address = "tnam1qrqh24mk3htevuqkqvsjc7xc3ast2rmghg8hqz2h",
    defaultPayAddr = "znam18zm29q7svz6lda6rlw0tph9zsrav6acjwqn7tmtmdqawmsuvut63kgerdftwearracxvvmm6r7n",
    assets = assetList1,
    activeAssetIndex = 0,
    estRewards = 23.421,
)
val account2 = Account(
    alias = "account 2",
    address = "tnam1qr5q8292rem8xz8qlrn4t6u28gxclpsnvcs9hjav",
    defaultPayAddr = "znam18zm29q7svz6lda6rlw0tph9zsrav6acjwqn7tmtmdqawmsuvut63kgerdftwearracxvvmm6r7n",
    assets = assetList2,
    activeAssetIndex = 1,
    estRewards = 1.45
)
val account3 = Account(
    alias = "account 3",
    address = "tnam1qph0exqucuq785m9h5lzwa84snjnpumt9ygy3z0d",
    defaultPayAddr = "znam1mgysdu759zklxrnt6d4zcd9u59sdr4x7cnfceqjgplrmxy7aaszuz9f6h6v74y8l7dtgv808ql3",
    assets = assetList3,
    activeAssetIndex = 0,
    estRewards = 14.432
)
val accountList = listOf(account1, account2, account3)
//////////

// Test App state
val testState = AppState.DataLoaded(
    accounts = accountList,
    networks = networkList,
    activeAccountIndex = 0,
    activeNetworkIndex = 0,
    namadaSdk = null,
    retrofit = null,
)

