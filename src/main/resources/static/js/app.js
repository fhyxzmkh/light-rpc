document.addEventListener('DOMContentLoaded', function() {
    // 导航菜单切换页面
    const navLinks = document.querySelectorAll('.nav-link');
    const pages = document.querySelectorAll('.page');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 切换导航激活状态
            navLinks.forEach(nav => nav.classList.remove('active'));
            this.classList.add('active');
            
            // 显示相应页面
            const targetPage = this.getAttribute('data-page');
            pages.forEach(page => {
                page.style.display = page.id === targetPage ? 'block' : 'none';
            });
            
            // 根据页面加载相应数据
            if (targetPage === 'job-list') {
                loadJobs();
            } else if (targetPage === 'company-list') {
                loadCompanies();
            }
        });
    });
    
    // 模态框实例
    const jobModal = new bootstrap.Modal(document.getElementById('jobDetailModal'));
    const companyModal = new bootstrap.Modal(document.getElementById('companyDetailModal'));
    
    // 加载招聘信息列表
    function loadJobs() {
        fetch('/api/jobs')
            .then(response => response.json())
            .then(jobs => {
                const jobCards = document.getElementById('job-cards');
                jobCards.innerHTML = '';
                
                jobs.forEach(job => {
                    const card = createJobCard(job);
                    jobCards.appendChild(card);
                });
            })
            .catch(error => {
                console.error('加载招聘信息失败:', error);
                alert('加载招聘信息失败，请稍后再试');
            });
    }
    
    // 创建招聘信息卡片
    function createJobCard(job) {
        const col = document.createElement('div');
        col.className = 'col-md-4';
        
        col.innerHTML = `
            <div class="card job-card">
                <div class="card-header bg-primary text-white">
                    ${job.title}
                </div>
                <div class="card-body">
                    <h5 class="card-title">${job.company}</h5>
                    <p class="card-text">
                        <span class="badge bg-success badge-salary">${job.salary}</span>
                        <span class="badge bg-info text-dark">${job.location}</span>
                    </p>
                    <button class="btn btn-primary view-job-btn" data-id="${job.id}">查看详情</button>
                </div>
            </div>
        `;
        
        // 绑定查看详情按钮点击事件
        col.querySelector('.view-job-btn').addEventListener('click', function() {
            const jobId = this.getAttribute('data-id');
            showJobDetail(jobId);
        });
        
        return col;
    }
    
    // 显示招聘详情
    function showJobDetail(jobId) {
        fetch(`/api/jobs/${jobId}`)
            .then(response => response.json())
            .then(job => {
                document.getElementById('jobModalTitle').textContent = job.title;
                
                const modalBody = document.getElementById('jobModalBody');
                modalBody.innerHTML = `
                    <div class="job-detail">
                        <p><strong>公司:</strong> ${job.company}</p>
                        <p><strong>地点:</strong> ${job.location}</p>
                        <p><strong>薪资:</strong> ${job.salary}</p>
                        <p><strong>职位描述:</strong></p>
                        <p>${job.description}</p>
                        <p><strong>联系方式:</strong> ${job.contact}</p>
                        <p><strong>发布时间:</strong> ${new Date(job.publishTime).toLocaleString()}</p>
                    </div>
                `;
                
                jobModal.show();
            })
            .catch(error => {
                console.error('加载职位详情失败:', error);
                alert('加载职位详情失败，请稍后再试');
            });
    }
    
    // 加载公司信息列表
    function loadCompanies() {
        fetch('/api/companies')
            .then(response => response.json())
            .then(companies => {
                const companyCards = document.getElementById('company-cards');
                companyCards.innerHTML = '';
                
                companies.forEach(company => {
                    const card = createCompanyCard(company);
                    companyCards.appendChild(card);
                });
            })
            .catch(error => {
                console.error('加载公司信息失败:', error);
                alert('加载公司信息失败，请稍后再试');
            });
    }
    
    // 创建公司信息卡片
    function createCompanyCard(company) {
        const col = document.createElement('div');
        col.className = 'col-md-4';
        
        col.innerHTML = `
            <div class="card company-card">
                <div class="card-header bg-info text-white">
                    ${company.name}
                </div>
                <div class="card-body">
                    <h5 class="card-title">${company.industry}</h5>
                    <p class="card-text">
                        <span class="badge bg-secondary">${company.scale}</span>
                        <span class="badge bg-light text-dark">${company.address}</span>
                    </p>
                    <button class="btn btn-info text-white view-company-btn" data-id="${company.id}">查看详情</button>
                </div>
            </div>
        `;
        
        // 绑定查看详情按钮点击事件
        col.querySelector('.view-company-btn').addEventListener('click', function() {
            const companyId = this.getAttribute('data-id');
            showCompanyDetail(companyId);
        });
        
        return col;
    }
    
    // 显示公司详情
    function showCompanyDetail(companyId) {
        fetch(`/api/companies/${companyId}`)
            .then(response => response.json())
            .then(company => {
                document.getElementById('companyModalTitle').textContent = company.name;
                
                const modalBody = document.getElementById('companyModalBody');
                modalBody.innerHTML = `
                    <div class="company-detail">
                        <p><strong>行业:</strong> ${company.industry}</p>
                        <p><strong>规模:</strong> ${company.scale}</p>
                        <p><strong>地址:</strong> ${company.address}</p>
                        <p><strong>公司简介:</strong></p>
                        <p>${company.description}</p>
                        <p><strong>联系方式:</strong> ${company.contact}</p>
                        <p><strong>公司网站:</strong> <a href="${company.website}" target="_blank">${company.website}</a></p>
                    </div>
                `;
                
                companyModal.show();
            })
            .catch(error => {
                console.error('加载公司详情失败:', error);
                alert('加载公司详情失败，请稍后再试');
            });
    }
    
    // 发布招聘信息表单提交
    const jobForm = document.getElementById('job-form');
    jobForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const jobData = {
            title: document.getElementById('title').value,
            company: document.getElementById('company').value,
            location: document.getElementById('location').value,
            salary: document.getElementById('salary').value,
            description: document.getElementById('description').value,
            contact: document.getElementById('contact').value
        };
        
        fetch('/api/jobs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jobData)
        })
        .then(response => response.json())
        .then(job => {
            alert('招聘信息发布成功！');
            jobForm.reset();
            
            // 切换到招聘列表页面
            document.querySelector('[data-page="job-list"]').click();
        })
        .catch(error => {
            console.error('发布招聘信息失败:', error);
            alert('发布招聘信息失败，请稍后再试');
        });
    });
    
    // 公司搜索功能
    const searchBtn = document.getElementById('search-btn');
    searchBtn.addEventListener('click', function() {
        const searchInput = document.getElementById('company-search-input').value;
        searchCompanies(searchInput);
    });
    
    // 搜索公司
    function searchCompanies(name) {
        fetch(`/api/companies/search?name=${encodeURIComponent(name)}`)
            .then(response => response.json())
            .then(companies => {
                const searchResults = document.getElementById('search-results');
                searchResults.innerHTML = '';
                
                if (companies.length === 0) {
                    searchResults.innerHTML = '<div class="alert alert-info">没有找到匹配的公司</div>';
                    return;
                }
                
                companies.forEach(company => {
                    const card = createCompanyCard(company);
                    searchResults.appendChild(card);
                });
            })
            .catch(error => {
                console.error('搜索公司失败:', error);
                alert('搜索公司失败，请稍后再试');
            });
    }
    
    // 默认加载招聘列表
    loadJobs();
});
