package com.lx.test.vo

/**
 *  date: 2022/12/7
 *  version: 1.0
 *  desc:
 */
/*
  {
"frontmatter": {
"banner": {
"childImageSharp": {
"fixed": {
"src": "/blog/static/86dc4fcf50aa69710dfb90fb74f922c1/11382/cover.jpg"
}
}
},
"categories": [
"news"
],
"date": "2022-11-02",
"language": "en",
"path": "/blog/en/post/2022/11/02/wallet-48-release",
"tags": [
"Wallet"
],
"title": "DID Wallet Release Notes"
}
}
 */
// 文章列表
class PostItemVo(
    val frontmatter: FrontmatterVo?
) {
    class FrontmatterVo(
        val banner: BannerVo?,
        val categories: List<String?>?,
        val date: String?,
        val language: String?,
        val path: String?,
        val tags: List<String?>?,
        val title: String?
    ) {
        class BannerVo(
            val childImageSharp: ChildImageSharpVo?
        ) {
            class ChildImageSharpVo(
                val fixed: FixedVo?
            ) {
                class FixedVo(
                    val src: String?
                )
            }
        }
    }
}







