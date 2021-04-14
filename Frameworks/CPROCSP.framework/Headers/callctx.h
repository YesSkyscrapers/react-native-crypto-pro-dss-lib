#ifndef CALLCTX_H_INCLUDED
#define CALLCTX_H_INCLUDED 1

#include<CPROCSP/BaseArithmDef_64.h>
#include<CPROCSP/compiler_attributes.h>

#ifdef __cplusplus
extern "C" {
#endif


typedef struct _CP_CALL_CTX_ CP_CALL_CTX, *pCP_CALL_CTX;
typedef struct _CRYPT_CSP_ CRYPT_CSP, * LPCRYPT_CSP;

typedef struct _CRYPT_CSP_THREAD_		CRYPT_CSP_THREAD,		*LPCRYPT_CSP_THREAD;
typedef struct _CSP_THREAD_TEMP_DATA		CSP_THREAD_TEMP_DATA,		*LPCSP_THREAD_TEMP_DATA;
typedef struct _CSP_THREAD_TEMP_CIPHER_DATA	CSP_THREAD_TEMP_CIPHER_DATA,	*LPCSP_THREAD_TEMP_CIPHER_DATA;
typedef struct _CSP_THREAD_TEMP_HASH_DATA	CSP_THREAD_TEMP_HASH_DATA,	*LPCSP_THREAD_TEMP_HASH_DATA;
typedef struct _CSP_THREAD_TEMP_PRSG_DATA	CSP_THREAD_TEMP_PRSG_DATA,	*LPCSP_THREAD_TEMP_PRSG_DATA;
typedef struct _CSP_THREAD_VTB_MP		CSP_THREAD_VTB_MP,		*LPCSP_THREAD_VTB_MP;

typedef struct _CP_ASTACK_ CP_ASTACK;



/* Предопределение структуры ДСЧ. */
struct RND_CONTEXT_;

/*! \internal
* \brief Указатель на функцию получения случайных чисел.
*
* \param context [in/out] Контекст ДСЧ, реально передается указатель на более
*  полную структуру контекста.
* \param buffer [out] Буфер, на выходе заполненный случайной последовательностью.
* \param length [in] Размер буфера под случайную последовательность.
* \return TRUE, случайная последовательность получена, FALSE иначе.
*/
typedef CSP_BOOL(*GetRandomFunction)(pCP_CALL_CTX pCallCtx, struct RND_CONTEXT_ *context, LPBYTE buffer, uint32_t length, uint32_t flags);


/*! \internal
* \brief Указатель на функцию проверки инициализированности ДСЧ.
*
* \param context [in/out] Контекст ДСЧ, реально передается указатель на более
*  полную структуру контекста.
* \return TRUE, случайная последовательность может быть получена (ДСЧ инициализирован),
*  FALSE иначе.
*/
typedef CSP_BOOL(*IsRandomInitedFunction)(struct RND_CONTEXT_ *context);

/*! \internal
* \brief Указатель на функцию получения состояния ДСЧ.
*
* \param context [in/out] Контекст ДСЧ, реально передается указатель на более
*  полную структуру контекста.
* \param seed [out] Инициализирующая последовательность.
*/
typedef CSP_BOOL(*GetRandomSeedFunction)(struct RND_CONTEXT_ *context, LPBYTE seed, size_t length);

/*! \internal
* \brief Указатель на функцию установки инициализирующей последовательности.
*
* \param context [in/out] Контекст ДСЧ, реально передается указатель на более
*  полную структуру контекста.
* \param seed [in] Инициализирующая последовательность.
*/
typedef CSP_BOOL(*SetRandomSeedFunction)(pCP_CALL_CTX pCallCtx, struct RND_CONTEXT_ *context, const LPBYTE seed, size_t length);
/*! \internal
* \brief Признак инициализации контекста от внешнего источника,
*  в частности с использованием функции CPSetProvParam(), PP_RANDOM.
*/
#define RND_INITED 0x00000001


/*! \internal
* \brief Контекст функции получения случайной последовательности.
*/
typedef struct RND_CONTEXT_ {
    GetRandomFunction make_random; /*!< Указатель на функцию получения случайного числа. */
    IsRandomInitedFunction is_inited; /*!< Указатель на функцию проверки инициализированности ДСЧ. */
    GetRandomSeedFunction get_random_seed; /*!< Указатель на функцию получения random seed. */
    SetRandomSeedFunction set_random_seed; /*!< Указатель на функцию установки random_seed. */
    DWORD Flags;			    /*!< Флаг состояния контекста.*/
} RND_CONTEXT, *LPRND_CONTEXT;

/* Реентерабельные варианты CSP_SetLastError/CSP_GetLastError */
void	rCSP_SetLastError	(pCP_CALL_CTX pCallCtx, DWORD err);
DWORD	rCSP_GetLastError	(pCP_CALL_CTX pCallCtx) ATTR_USERES;

#ifdef USE_STATIC_ANALYZER
#define rAllocMemory(pCallCtx,dwSize,dwMemPoolId) (pCallCtx, dwMemPoolId,calloc(dwSize, 1))
#define rFreeMemory(pCallCtx,pMem,dwMemPoolId)  (pCallCtx, dwMemPoolId,free(pMem))
#else
/* Реентерабельные варианты AllocMemory/FreeMemory */
LPVOID	rAllocMemory	(pCP_CALL_CTX pCallCtx, size_t dwSize, DWORD dwMemPoolId) ATTR_USERES;
void	rFreeMemory	(pCP_CALL_CTX pCallCtx, VOID *pMem, DWORD dwMemPoolId);
#endif

/*! Инициализация памяти */
CSP_BOOL	rInitMemory	(pCP_CALL_CTX pCallCtx) ATTR_USERES;

#if !defined UNIX
/*! Уничтожение всех куч */
void	rDoneMemory	(pCP_CALL_CTX pCallCtx);
#endif /* !UNIX */
/*! Проверка целостности куч */
void	rValidateMemory	(pCP_CALL_CTX pCallCtx);

void	rInitCallCtx	(pCP_CALL_CTX pCallCtx, LPCRYPT_CSP hCSP);


struct _CP_CALL_CTX_
{
    LPCRYPT_CSP			hCSP;
    LPCRYPT_CSP_THREAD		hCSPthread; // логичнее, чем каждый раз переходить к основному hCSP через hCSPthread
    LPRND_CONTEXT		ThreadPRSG; // Чтобы можно было подменять ДСЧ в макросе MakeRandom
    DWORD			dwError;
    DWORD			dwThreadId;
    DWORD			dwCommonKArrayLength;
    BYTE                       *pbCommonKArray; 
    CSP_BOOL			bOwnFPU;
    int				iCntFPU;
    DWORD			dwFPUOpType;
	// ASSERT - адрес выровнен на FPUAllignValue
    struct cp_vtb_28147_89     *pVTB;
    CP_ASTACK	       *pAStk;
};


#ifdef __cplusplus
}
#endif

#endif /* CALLCTX_H_INCLUDED */
