package org.superapp.negotiatorbot.webclient.promt

const val COMPANY_INFO = "Please describe all the data that you can find on the provided documents"
   const val propmt2 = "Представленные документы - учредительные документы компании в юрисдикции РФ. Из представленных документов, верни только официальное название компании, или ОГРН, или ОГРНИП, или ИНН. Не возвращай больше ничего, ни единого слова. Формат: НАЙДЕНО=???"
const val prompt = "from provided docs, return only ОГРН or ОГРНИП strictly as a number. dont return nothng else. not a single word. only this number. format: OGRN=???"